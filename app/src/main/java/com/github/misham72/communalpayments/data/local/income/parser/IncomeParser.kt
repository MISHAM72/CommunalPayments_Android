package com.github.misham72.communalpayments.data.local.income.parser

import com.github.misham72.communalpayments.data.common.DataConstants
import com.github.misham72.communalpayments.domain.model.Attachment
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
            val date = extractDate(block) ?: continue
            val source = extractSource(block) ?: continue
            val amount = extractAmount(block) ?: continue
            records.add(
                IncomeRecord(
                    date = date,
                    amount = amount,
                    source = source,
                    attachments = extractAttachments(block)
                )
            )
        }
        return records
    }

    private fun extractDate(block: String): LocalDate? {
        for (line in block.lines()) {
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

    private fun extractSource(block: String): String? {
        @Suppress("HardcodedStringLiteral")
        val regex = Regex("""Источник:\s*(.+)""")
        return regex.find(block)?.groupValues?.get(1)?.trim()
    }

    private fun extractAmount(block: String): Double? {
        for (line in block.lines()) {
            if (line.trim().startsWith(DataConstants.LABEL_AMOUNT)) {
                val raw = line.substringAfter(DataConstants.LABEL_AMOUNT).trim()
                    .replace(" ", "")
                    .replace(",", ".")
                return raw.toDoubleOrNull()
            }
        }
        return null
    }

    /**
     * Читает вложения. Поддерживает два формата:
     *
     * 1) НОВЫЙ: одна строка на вложение
     *    Вложение: /path/to/file.jpg|photo.jpg|image/jpeg
     *
     * 2) СТАРЫЙ: три отдельные строки для одного вложения
     *    Вложение: /path/to/file.jpg
     *    ИмяФайла: photo.jpg
     *    MimeType: image/jpeg
     */
    private fun extractAttachments(block: String): List<Attachment> {
        val lines = block.lines()

        // 1. Пробуем новый формат (с разделителями "|").
        @Suppress("HardcodedStringLiteral")
        val newFormatRegex = Regex("""Вложение:\s*(.+?)\|(.+?)\|(.+)""")
        val newAttachments = lines.mapNotNull { line ->
            val match = newFormatRegex.find(line.trim()) ?: return@mapNotNull null
            Attachment(
                path = match.groupValues[1].trim(),
                name = match.groupValues[2].trim(),
                mimeType = match.groupValues[3].trim()
            )
        }
        if (newAttachments.isNotEmpty()) return newAttachments

        // 2. Fallback — старый формат (одно вложение, 3 строки)
        @Suppress("HardcodedStringLiteral")
        val oldPathRegex = Regex("""Вложение:\s*(.+)""")

        @Suppress("HardcodedStringLiteral")
        val oldNameRegex = Regex("""ИмяФайла:\s*(.+)""")

        @Suppress("HardcodedStringLiteral")
        val oldMimeRegex = Regex("""MimeType:\s*(.+)""")

        val path = oldPathRegex.find(block)?.groupValues?.get(1)?.trim()
        val name = oldNameRegex.find(block)?.groupValues?.get(1)?.trim()
        val mime = oldMimeRegex.find(block)?.groupValues?.get(1)?.trim()

        return if (path != null) {
            listOf(
                Attachment(
                    path = path,
                    name = name ?: path.substringAfterLast('/'),
                    mimeType = mime ?: "application/octet-stream"
                )
            )
        } else emptyList()
    }
}
