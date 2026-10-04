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

class HistoryParser(
    private val statusCalculated: String,
    private val currentReadingPdf: String,
    private val previousReadingPdf: String,
    private val consumptionPdf: String,
    private val toBePaid: String,
    private val tariff: String,
    private val periodPdf: String,
    private val nextPaymentPdf: String,
) {

    private val separator: String = DataConstants.HISTORY_SEPARATOR
        .repeat(DataConstants.HISTORY_SEPARATOR_COUNT)

    private val dateTimeRegex = Regex(DataConstants.DATE_TIME_REGEX_PATTERN)

    private val attachmentPathRegex = Regex("""^${HistoryFormat.ATTACHMENT_PREFIX}(\d+):\s*(.+)$""")
    private val attachmentNameRegex = Regex("""^${HistoryFormat.FILE_NAME_PREFIX}(\d+):\s*(.+)$""")
    private val attachmentMimeRegex = Regex("""^${HistoryFormat.MIME_TYPE_PREFIX}(\d+):\s*(.+)$""")
    private val consumptionRegex = Regex("""Расход:?\s*-?\s*[\d.,]+""")

    fun extractLatestConsumption(content: String): Double? {
        if (content.isBlank()) return null
        val match = consumptionRegex.find(content) ?: return null
        val number = match.value.replace(Regex("""[^\d.,]"""), "")
        return number.replace(',', '.').toDoubleOrNull()
    }

    fun parse(content: String, serviceKey: String): List<HistoryRecord> {
        if (content.isBlank()) return emptyList()

        val parts = content.split(separator)
        val records = mutableListOf<HistoryRecord>()

        parts.forEachIndexed { index, part ->
            if (part.isBlank()) return@forEachIndexed

            val block = if (index == 0) part else separator + part
            val dateMatch = dateTimeRegex.find(block) ?: return@forEachIndexed

            val attachments = extractAttachments(block)
            val fields = parseFields(block)

            records.add(
                HistoryRecord(
                    rawBlock = block.trimEnd(),
                    dateTime = dateMatch.value,
                    serviceKey = serviceKey,
                    attachments = attachments,
                    status = fields.status,
                    amount = fields.amount,
                    tariff = fields.tariff,
                    currentReading = fields.currentReading,
                    previousReading = fields.previousReading,
                    consumption = fields.consumption,
                    nextPayment = fields.nextPayment,
                    periodMonths = fields.periodMonths,
                )
            )
        }
        return records
    }

    // Внутренний контейнер для распарсенных полей
    private data class ParsedFields(
        val status: String = "",
        val amount: String = "",
        val tariff: String = "",
        val currentReading: String = "",
        val previousReading: String = "",
        val consumption: String = "",
        val nextPayment: String = "",
        val periodMonths: String = "",
    )

    private fun parseFields(block: String): ParsedFields {
        var status = statusCalculated
        var amount = ""
        var tariffValue = ""
        var currentReading = ""
        var previousReading = ""
        var consumption = ""
        var nextPayment = ""
        var periodMonths = ""

        block.lines().forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith(currentReadingPdf) -> currentReading = trimmed.substringAfter(":").trim()
                trimmed.startsWith(previousReadingPdf) -> previousReading = trimmed.substringAfter(":").trim()
                trimmed.startsWith(consumptionPdf) -> consumption = trimmed.substringAfter(":").trim()
                trimmed.startsWith(toBePaid) -> amount = trimmed.substringAfter(":").trim()
                trimmed.startsWith(tariff) -> tariffValue = trimmed.substringAfter(":").trim()
                trimmed.startsWith(periodPdf) -> periodMonths = trimmed.substringAfter(":").trim()
                trimmed.startsWith(nextPaymentPdf) -> nextPayment = trimmed.substringAfter(":").trim()

                trimmed.startsWith("\uD83D\uDD34") -> status = trimmed   // 🔴
                trimmed.startsWith("\u23F3") -> status = trimmed          // ⏳
                trimmed.startsWith("\u2705") -> status = trimmed          // ✅
                trimmed.startsWith("\uD83D\uDD0D") -> status = trimmed    // 🔍
                trimmed.startsWith("\uD83D\uDEAB") -> status = trimmed    // 🚫
                trimmed.startsWith("\uD83E\uDD13") -> status = trimmed    // 🤓
            }
        }

        return ParsedFields(
            status = status,
            amount = amount,
            tariff = tariffValue,
            currentReading = currentReading,
            previousReading = previousReading,
            consumption = consumption,
            nextPayment = nextPayment,
            periodMonths = periodMonths,
        )
    }

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

        return pathMap.keys.sorted().mapNotNull { idx ->
            val path = pathMap[idx] ?: return@mapNotNull null
            val name = nameMap[idx] ?: return@mapNotNull null
            val mime = mimeMap[idx] ?: return@mapNotNull null
            Attachment(path = path, name = name, mimeType = mime)
        }
    }

    fun updateBlockAttachments(
        block: String, attachments: List<Attachment>
    ): String {

        val linesWithoutAttachments = block.lines().filterNot {
            val t = it.trim()
            t.startsWith(HistoryFormat.ATTACHMENT) || t.startsWith(HistoryFormat.FILE_NAME) || t.startsWith(HistoryFormat.MIME_TYPE) || attachmentPathRegex.matches(t) || attachmentNameRegex.matches(t) || attachmentMimeRegex.matches(t)
        }.toMutableList()

        while (linesWithoutAttachments.isNotEmpty() && linesWithoutAttachments.last().isBlank()) {
            linesWithoutAttachments.removeAt(linesWithoutAttachments.lastIndex)
        }

        attachments.forEachIndexed { i, att ->
            val n = i + 1
            linesWithoutAttachments.add("Вложение$n: ${att.path}")
            linesWithoutAttachments.add("ИмяФайла$n: ${att.name}")
            linesWithoutAttachments.add("MimeType$n: ${att.mimeType}")
        }

        return linesWithoutAttachments.joinToString("\n")
    }
}
