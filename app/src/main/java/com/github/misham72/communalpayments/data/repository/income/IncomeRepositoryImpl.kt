package com.github.misham72.communalpayments.data.repository.income

import com.github.misham72.communalpayments.data.common.DataConstants
import com.github.misham72.communalpayments.data.local.income.filemanager.IncomeFileManager
import com.github.misham72.communalpayments.data.local.income.parser.IncomeParser
import com.github.misham72.communalpayments.domain.model.Attachment
import com.github.misham72.communalpayments.domain.model.incomes.IncomeCategory
import com.github.misham72.communalpayments.domain.model.incomes.IncomeRecord
import com.github.misham72.communalpayments.domain.model.incomes.IncomeSummary
import com.github.misham72.communalpayments.domain.repository.IncomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate

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


    override suspend fun addIncome(year: Int, date: LocalDate, source: String, amount: Double) {
        val record = buildBlock(IncomeRecord(date = date, amount = amount, source = source))
        withContext(Dispatchers.IO) {
            fileManager.appendIncome(year, record)
        }
    }

    override suspend fun updateIncome(year: Int, content: String) {
        fileManager.saveIncome(year, content)
    }

    override suspend fun updateRecord(year: Int, oldRecord: IncomeRecord, newRecord: IncomeRecord) {
        val raw = fileManager.readIncome(year)
        val oldBlock = findBlock(raw, oldRecord) ?: return
        val newBlock = buildBlock(newRecord)
        val updated = raw.replace(oldBlock, newBlock)
        fileManager.saveIncome(year, updated)
    }

    override suspend fun deleteRecord(year: Int, record: IncomeRecord) {
        val raw = fileManager.readIncome(year)
        val block = findBlock(raw, record) ?: return
        val updated = raw
            .replace("$block\n***\n", "")
            .replace("\n***\n$block", "")
            .replace(block, "")
        fileManager.saveIncome(year, updated)
    }

    /**
     * Находит блок в тексте файла по дате, источнику и сумме.
     * Возвращает полный текст блока (с вложениями, в любом формате).
     */
    private fun findBlock(raw: String, record: IncomeRecord): String? {
        val blocks = raw.split("\n***\n").filter { it.isNotBlank() }
        val targetDate = record.date.toString()
        val targetSource = record.source
        val targetAmount = DataConstants.AMOUNT_FORMAT.format(record.amount).replace(',', '.')

        for (block in blocks) {
            val lines = block.lines()
            val hasDate = lines.any { it.trim() == targetDate }

            @Suppress("HardcodedStringLiteral")
            val hasSource = lines.any { it.trim() == "Источник: $targetSource" }

            @Suppress("HardcodedStringLiteral")
            val hasAmount = lines.any { it.trim().startsWith("Сумма:") && it.contains(targetAmount) }

            if (hasDate && hasSource && hasAmount) return block
        }
        return null
    }

    override suspend fun deleteAllRecordsBySource(year: Int, source: String) {
        val raw = fileManager.readIncome(year)
        val blocks = raw.split("\n***\n").filter { it.isNotBlank() }
        val filtered = blocks.filter { block ->
            val lines = block.lines()

            @Suppress("HardcodedStringLiteral")
            val blockSource = lines.firstOrNull { it.startsWith("Источник:") }?.substringAfter("Источник:")?.trim()
            blockSource != source
        }
        val updated = filtered.joinToString("\n***\n")
        fileManager.saveIncome(year, updated)
    }

    // Вспомогательная функция для форматирования записи в текстовый блок
    private fun buildBlock(record: IncomeRecord): String {
        val dateString = record.date.toString()
        val formattedAmount = DataConstants.AMOUNT_FORMAT.format(record.amount).replace(',', '.')
        return buildString {
            appendLine(dateString)
            appendLine("Источник: ${record.source}")
            appendLine("Сумма: $formattedAmount")
            record.attachments.forEach { att ->
                @Suppress("HardcodedStringLiteral")
                appendLine("Вложение: ${att.path}|${att.name}|${att.mimeType}")
            }
        }.trimEnd()
    }

    override suspend fun getRecordsByYear(year: Int): List<IncomeRecord> {
        val raw = fileManager.readIncome(year)
        return IncomeParser.parse(raw)
    }

    override suspend fun attachAttachment(
        year: Int,
        record: IncomeRecord,
        bytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        // 1. Сохраняем файл на диск
        val path = fileManager.saveAttachment(year, bytes, fileName)

        // 2. Создаём объект Attachment
        val newAttachment = Attachment(
            path = path,
            name = fileName,
            mimeType = mimeType
        )

        // 3. Создаём новый record с добавленным вложением
        val updatedRecord = record.copy(attachments = record.attachments + newAttachment)

        // 4. Обновляем текстовый файл
        updateRecord(year, record, updatedRecord)
    }

    override suspend fun removeAttachment(
        year: Int,
        record: IncomeRecord,
        attachment: Attachment
    ) {
        // 1. Удаляем файл с диска
        fileManager.deleteAttachment(attachment.path)

        // 2. Создаём новый record без этого вложения
        val updatedRecord = record.copy(
            attachments = record.attachments.filter { it.path != attachment.path }
        )

        // 3. Обновляем текстовый файл
        updateRecord(year, record, updatedRecord)
    }

    override fun getAttachment(path: String): File? {
        return fileManager.getAttachment(path)
    }
}
