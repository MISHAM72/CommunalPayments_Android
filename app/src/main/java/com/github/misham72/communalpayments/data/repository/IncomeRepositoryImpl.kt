package com.github.misham72.communalpayments.data.repository

import com.github.misham72.communalpayments.data.local.IncomeFileManager
import com.github.misham72.communalpayments.data.local.IncomeParser
import com.github.misham72.communalpayments.domain.model.IncomeCategory
import com.github.misham72.communalpayments.domain.model.IncomeSummary
import com.github.misham72.communalpayments.domain.repository.IncomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class IncomeRepositoryImpl(
    private val fileManager: IncomeFileManager
) : IncomeRepository {

    override suspend fun getYearlyIncome(year: Int): IncomeSummary {
        val rawText = fileManager.readIncome(year)
        val records = IncomeParser.parse(rawText)

        val bySourceString = records.groupBy { it.source }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val byCategory = bySourceString.mapKeys { (key, _) ->
            try {
                enumValueOf<IncomeCategory>(key)
            } catch (e: IllegalArgumentException) {
                // Если встретилась старая русская строка (например, "Зарплата"),
                // маппим в OTHER и игнорируем – данные потеряются.
                IncomeCategory.OTHER
            }
        }

        val total = byCategory.values.sum()
        val nonEmptyCategories = byCategory.filter { it.value > 0.0 }
        val average = if (nonEmptyCategories.isNotEmpty()) total / nonEmptyCategories.size else 0.0
        val maxSource = nonEmptyCategories.maxByOrNull { it.value }?.key
        val minSource = nonEmptyCategories.minByOrNull { it.value }?.key

        return IncomeSummary(
            total = total,
            bySource = byCategory,
            average = average,
            maxSource = maxSource,
            minSource = minSource
        )
    }

    @Suppress("HardcodedStringLiteral")
    override suspend fun addIncome(year: Int, date: LocalDate, source: String, amount: Double) {
        val formattedAmount = "%.2f".format(amount).replace(',', '.')
        val record = "${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}\nИсточник: $source\nСумма: $formattedAmount"
        withContext(Dispatchers.IO) {
            fileManager.appendIncome(year, record)
        }
    }

    override suspend fun updateIncome(year: Int, content: String) {
        fileManager.saveIncome(year, content)
    }
}
