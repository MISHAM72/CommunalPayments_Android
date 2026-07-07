package com.github.misham72.communalpayments.data.local

import com.github.misham72.communalpayments.domain.model.PaymentRecord
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

object HistoryParser {
    @Suppress("HardcodedStringLiteral")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private const val BLOCK_SEPARATOR = "***"

    fun parse(rawText: String, paidEmoji: String): List<PaymentRecord> {
        val records = mutableListOf<PaymentRecord>()
        val blocks = rawText.split(BLOCK_SEPARATOR).filter { it.isNotBlank() }

        for (block in blocks) {
            // Пропускаем блоки без эмодзи оплаты
            if (!block.contains(paidEmoji)) continue

            val amount = extractAmount(block)
            val date = extractDate(block)

            if (amount != null && date != null) {
                records.add(PaymentRecord(YearMonth.from(date), amount))
            }
        }
        return records
    }

    @Suppress("HardcodedStringLiteral")
    private fun extractAmount(block: String): Double? {
        val lines = block.lines()
        // Сначала ищем фразы, характерные для счётчиков (русские и английские)
        for (line in lines) {
            if (line.contains("К оплате:") || line.contains("To be paid:") ||
                line.contains("Сумма к оплате:") || line.contains("Amount due:")
            ) {
                return extractNumber(line)
            }
        }
        // Затем для периодических услуг (тарифы, оплаты и т.п.)
        for (line in lines) {
            if (line.contains("Тариф:") || line.contains("Tariff:") ||
                line.contains("Оплата:") || line.contains("Payment:") ||
                line.contains("Сумма:") || line.contains("Amount:")
            ) {
                return extractNumber(line)
            }
        }
        return null
    }

    @Suppress("HardcodedStringLiteral")
    private fun extractNumber(line: String): Double? {
        val cleaned = line.replace(" ", "").replace(",", ".")
        val regex = Regex("""\d+(?:\.\d{1,2})?""")
        return regex.find(cleaned)?.value?.toDoubleOrNull()
    }

    private fun extractDate(block: String): LocalDate? {
        val lines = block.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.length >= 10 && trimmed[4] == '-' && trimmed[7] == '-') {
                try {
                    return LocalDate.parse(trimmed.substring(0, 10), dateFormatter)
                } catch (_: Exception) {
                }
            }
        }
        return null
    }
}
