package com.github.misham72.communalpayments.data.repository.income

import com.github.misham72.communalpayments.data.local.income.filemanager.IncomeFileManager
import com.github.misham72.communalpayments.data.local.income.parser.IncomeParser
import com.github.misham72.communalpayments.domain.model.incomes.IncomeCategory
import com.github.misham72.communalpayments.domain.model.incomes.IncomeRecord
import com.github.misham72.communalpayments.domain.model.incomes.IncomeSummary
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

    override suspend fun updateRecord(year: Int, oldRecord: IncomeRecord, newRecord: IncomeRecord) {
        val raw = fileManager.readIncome(year)
        val oldBlock = buildBlock(oldRecord)
        val newBlock = buildBlock(newRecord)
        val updated = raw.replace(oldBlock, newBlock)
        fileManager.saveIncome(year, updated)
    }

    override suspend fun deleteRecord(year: Int, record: IncomeRecord) {
        val raw = fileManager.readIncome(year)
        val block = buildBlock(record)
        val updated = raw.replace("$block\n***", "").replace(block, "")
        fileManager.saveIncome(year, updated)
    }

    override suspend fun deleteAllRecordsBySource(year: Int, source: String) {
        val raw = fileManager.readIncome(year)
        val blocks = raw.split("\n***\n").filter { it.isNotBlank() }
        val filtered = blocks.filter { block ->
            val lines = block.lines()
            val blockSource = lines.firstOrNull { it.startsWith("Источник:") }?.substringAfter("Источник:")?.trim()
            blockSource != source
        }
        val updated = filtered.joinToString("\n***\n")
        fileManager.saveIncome(year, updated)
    }

    // Вспомогательная функция для форматирования записи в текстовый блок
    private fun buildBlock(record: IncomeRecord): String {
        val dateString = record.date.toString()
        val formattedAmount = "%.2f".format(record.amount).replace(',', '.')
        return "$dateString\nИсточник: ${record.source}\nСумма: $formattedAmount"
    }

    override suspend fun getRecordsByYear(year: Int): List<IncomeRecord> {
        val raw = fileManager.readIncome(year)
        return IncomeParser.parse(raw)
    }
}
