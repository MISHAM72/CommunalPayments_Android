package com.github.misham72.communalpayments.data.repository.analytics

import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.domain.model.YearSummary
import com.github.misham72.communalpayments.domain.repository.AnalyticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Suppress("HardcodedStringLiteral")
class AnalyticsRepositoryImpl(private val fileManager: FileManager) : AnalyticsRepository {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override suspend fun getYearSummary(serviceKey: String, year: Int): YearSummary {
        return withContext(Dispatchers.IO) {
            calculateYearSummary(serviceKey, year)
        }
    }

    override suspend fun getAllServicesYearSummary(serviceKeys: List<String>, year: Int): Map<String, YearSummary> {
        return withContext(Dispatchers.IO) {
            val result = mutableMapOf<String, YearSummary>()
            for (key in serviceKeys) {
                result[key] = calculateYearSummary(key, year)
            }
            result
        }
    }

    private suspend fun calculateYearSummary(serviceKey: String, year: Int): YearSummary {
        val rawText = fileManager.readHistory(serviceKey)
        val monthly = mutableMapOf<Int, Double>()
        var total = 0.0

        // Разделитель – 14 зелёных квадратов
        val blocks = rawText.split("🟩".repeat(14)).filter { it.isNotBlank() }

        for (block in blocks) {
            // Берём только оплаченные блоки (есть 🔴)
            if (!block.contains("🔴")) continue

            val lines = block.lines()
            var dateStr: String? = null
            var amount: Double? = null

            for (line in lines) {
                val trimmed = line.trim()
                // Дата вида 2026-06-28 23:29:25
                if (trimmed.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"))) {
                    dateStr = trimmed
                }
                @Suppress("HardcodedStringLiteral")
                // Сначала ищем "К оплате:", потом "Тариф:"
                if (trimmed.startsWith("К оплате") || trimmed.startsWith("К оплате:")) {
                    amount = extractNumber(trimmed)
                } else if (amount == null && (trimmed.startsWith("Тариф:") || trimmed.startsWith("Тариф"))) {
                    amount = extractNumber(trimmed)
                }
            }

            if (dateStr != null && amount != null) {
                try {
                    val date = LocalDate.parse(dateStr, dateFormatter)
                    if (date.year == year) {
                        val month = date.monthValue
                        monthly[month] = (monthly[month] ?: 0.0) + amount
                        total += amount
                    }
                } catch (_: Exception) {
                    // игнорируем неверные даты
                }
            }
        }

        val average = if (monthly.isNotEmpty()) monthly.values.average() else 0.0
        val maxMonth = monthly.maxByOrNull { it.value }?.key
        val minMonth = monthly.minByOrNull { it.value }?.key

        return YearSummary(
            total = total,
            monthly = monthly,
            average = average,
            maxMonth = maxMonth,
            minMonth = minMonth
        )
    }

    // Вспомогательная функция для извлечения числа из строки
    private fun extractNumber(line: String): Double? {
        val cleaned = line.replace(" ", "").replace(",", ".")
        val regex = Regex("""\d+(?:\.\d{1,2})?""")
        return regex.find(cleaned)?.value?.toDoubleOrNull()
    }
}
