@file:Suppress("HardcodedStringLiteral")

package com.github.misham72.communalpayments.data.parser


import com.github.misham72.communalpayments.data.common.DataConstants
import com.github.misham72.communalpayments.domain.model.Attachment
import com.github.misham72.communalpayments.domain.model.HistoryRecord

@Suppress("HardcodedStringLiteral")
object HistoryFormat {
    const val ATTACHMENT = "Вложение:"
    const val FILE_NAME = "ИмяФайла:"
    const val MIME_TYPE = "MimeType:"
    const val ATTACHMENT_PREFIX = "Вложение"
    const val FILE_NAME_PREFIX = "ИмяФайла"
    const val MIME_TYPE_PREFIX = "MimeType"
}

object HistoryParser {

    private val SEPARATOR: String = DataConstants.HISTORY_SEPARATOR
        .repeat(DataConstants.HISTORY_SEPARATOR_COUNT)

    private val dateTimeRegex = Regex(DataConstants.DATE_TIME_REGEX_PATTERN)

    // Регулярка для строк вида "Вложение1: /path/to/file", "ИмяФайла2: ...", "MimeType3: ..."
    private val attachmentPathRegex = Regex("""^${HistoryFormat.ATTACHMENT_PREFIX}(\d+):\s*(.+)$""")
    private val attachmentNameRegex = Regex("""^${HistoryFormat.FILE_NAME_PREFIX}(\d+):\s*(.+)$""")
    private val attachmentMimeRegex = Regex("""^${HistoryFormat.MIME_TYPE_PREFIX}(\d+):\s*(.+)$""")
    private val CONSUMPTION_REGEX = Regex("""Расход:?\s*-?\s*([\d.,]+)""")

    fun extractLatestConsumption(content: String): Double? {
        if (content.isBlank()) return null
        val match = CONSUMPTION_REGEX.find(content) ?: return null
        return match.groupValues[1].replace(',', '.').toDoubleOrNull()
    }
    fun parse(content: String, serviceKey: String): List<HistoryRecord> {
        if (content.isBlank()) return emptyList()

        val parts = content.split(SEPARATOR)
        val records = mutableListOf<HistoryRecord>()

        parts.forEachIndexed { index, part ->
            if (part.isBlank()) return@forEachIndexed

            val block = if (index == 0) part else SEPARATOR + part
            val dateMatch = dateTimeRegex.find(block) ?: return@forEachIndexed

            val attachments = extractAttachments(block)

            records.add(
                HistoryRecord(
                    rawBlock = block.trimEnd(),
                    dateTime = dateMatch.value,
                    serviceKey = serviceKey,
                    attachments = attachments
                )
            )
        }
        return records
    }

    /**
     * Достаёт все вложения из блока.
     * Поддерживает сколько угодно вложений: Вложение1, Вложение2, ...
     */
    private fun extractAttachments(block: String): List<Attachment> {
        val pathMap = mutableMapOf<Int, String>()
        val nameMap = mutableMapOf<Int, String>()
        val mimeMap = mutableMapOf<Int, String>()

        block.lines().forEach { line ->
            val trimmed = line.trim()

            attachmentPathRegex.find(trimmed)?.let { m ->
                val idx = m.groupValues[1].toIntOrNull() ?: return@let
                pathMap[idx] = m.groupValues[2].trim()
            }
            attachmentNameRegex.find(trimmed)?.let { m ->
                val idx = m.groupValues[1].toIntOrNull() ?: return@let
                nameMap[idx] = m.groupValues[2].trim()
            }
            attachmentMimeRegex.find(trimmed)?.let { m ->
                val idx = m.groupValues[1].toIntOrNull() ?: return@let
                mimeMap[idx] = m.groupValues[2].trim()
            }
        }

        // Собираем вложения, у которых есть все три поля
        return pathMap.keys
            .sorted()
            .mapNotNull { idx ->
                val path = pathMap[idx] ?: return@mapNotNull null
                val name = nameMap[idx] ?: return@mapNotNull null
                val mime = mimeMap[idx] ?: return@mapNotNull null
                Attachment(path = path, name = name, mimeType = mime)
            }
    }

    /**
     * Обновляет строки вложений в блоке.
     * Полностью заменяет все строки Вложение/ИмяФайла/MimeType на новые.
     */

    fun updateBlockAttachments(
        block: String,
        attachments: List<Attachment>
    ): String {

        // Убираем все старые строки вложений
        val linesWithoutAttachments = block.lines()
            .filterNot {
                val t = it.trim()
                t.startsWith(HistoryFormat.ATTACHMENT) ||         // старая версия (одно)
                    t.startsWith(HistoryFormat.FILE_NAME) ||
                    t.startsWith(HistoryFormat.MIME_TYPE) ||
                    attachmentPathRegex.matches(t) ||
                    attachmentNameRegex.matches(t) ||
                    attachmentMimeRegex.matches(t)
            }
            .toMutableList()

        // Удаляем пустые строки в конце
        while (linesWithoutAttachments.isNotEmpty() &&
            linesWithoutAttachments.last().isBlank()
        ) {
            linesWithoutAttachments.removeAt(linesWithoutAttachments.lastIndex)
        }

        // Добавляем новые вложения с индексами 1, 2, 3...
        attachments.forEachIndexed { i, att ->
            val n = i + 1
            linesWithoutAttachments.add("Вложение$n: ${att.path}")
            linesWithoutAttachments.add("ИмяФайла$n: ${att.name}")
            linesWithoutAttachments.add("MimeType$n: ${att.mimeType}")
        }

        return linesWithoutAttachments.joinToString("\n")
    }
}
