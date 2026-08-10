package com.github.misham72.communalpayments.data.local.income.parser

import com.github.misham72.communalpayments.domain.model.incomes.IncomeRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object IncomeParser {
    @Suppress("HardcodedStringLiteral")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private const val BLOCK_SEPARATOR = "***"

    fun parse(rawText: String): List<IncomeRecord> {
        val records = mutableListOf<IncomeRecord>()
        val blocks = rawText.split(BLOCK_SEPARATOR).filter { it.isNotBlank() }
        for (block in blocks) {
            val date = extractDate(block)
            val source = extractSource(block)
            val amount = extractAmount(block)
            if (date != null && source != null && amount != null) {
                records.add(IncomeRecord(date, amount, source))
            }
        }
        return records
    }

    private fun extractDate(block: String): LocalDate? {
        val lines = block.lines()
        for (line in lines) {
            val trimmed = line.trim()
            // Проверка на формат yyyy-MM-dd в начале строки
            if (trimmed.length >= 10 && trimmed[4] == '-' && trimmed[7] == '-') {
                try {
                    return LocalDate.parse(trimmed.substring(0, 10), dateFormatter)
                } catch (_: Exception) {
                }
            }
        }
        return null
    }

    private fun extractSource(block: String): String? {
        @Suppress("HardcodedStringLiteral")
        val regex = Regex("""Источник:\s*(.+)""")
        val match = regex.find(block) ?: return null
        return match.groupValues[1].trim()
    }

    @Suppress("HardcodedStringLiteral")
    private fun extractAmount(block: String): Double? {
        val lines = block.lines()
        for (line in lines) {
            if (line.trim().startsWith("Сумма:")) {
                val raw = line.substringAfter("Сумма:").trim()
                    .replace(" ", "")
                    .replace(",", ".")   // допускает как запятую, так и точку
                // Теперь raw может быть "1400", "1400.00", "1400,00" и т.д.
                return raw.toDoubleOrNull()
            }
        }
        return null
    }
}
