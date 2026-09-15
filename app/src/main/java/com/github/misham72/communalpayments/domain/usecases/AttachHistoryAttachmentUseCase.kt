package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.parser.HistoryParser
import com.github.misham72.communalpayments.domain.model.Attachment
import java.io.ByteArrayInputStream

/**
 * Прикрепляет НОВОЕ вложение к записи истории.
 * Существующие вложения НЕ удаляются — новое добавляется в конец списка.
 */
class AttachHistoryAttachmentUseCase(
    private val fileManager: FileManager
) {
    suspend operator fun invoke(
        serviceKey: String,
        rawBlock: String,
        currentAttachments: List<Attachment>,   // ← существующие вложения
        bytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        // 1. Сохранить файл на диск
        val savedPath = fileManager.saveHistoryAttachment(
            inputStream = ByteArrayInputStream(bytes),
            serviceKey = serviceKey,
            fileName = fileName
        )

        // 2. Сформировать новый список: существующие + новое
        val newAttachment = Attachment(
            path = savedPath,
            name = fileName,
            mimeType = mimeType
        )
        val updatedAttachments = currentAttachments + newAttachment

        // 3. Обновить блок в тексте истории
        val newBlock = HistoryParser.updateBlockAttachments(
            block = rawBlock,
            attachments = updatedAttachments
        )

        // 4. Заменить в файле истории
        val content = fileManager.readHistory(serviceKey)
        if (!content.contains(rawBlock)) {
            throw IllegalStateException(
                "Блок записи не найден в истории. Возможно, файл был изменён."
            )
        }
        val updated = content.replaceFirst(rawBlock, newBlock)
        fileManager.saveHistory(serviceKey, updated)
    }
}
