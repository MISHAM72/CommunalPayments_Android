package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.parser.HistoryParser
import com.github.misham72.communalpayments.domain.model.Attachment

/**
 * Удаляет КОНКРЕТНОЕ вложение из записи истории.
 * Остальные вложения сохраняются.
 */
class RemoveHistoryAttachmentUseCase(
    private val fileManager: FileManager
) {
    suspend operator fun invoke(
        serviceKey: String,
        rawBlock: String,
        currentAttachments: List<Attachment>,
        attachmentToRemove: Attachment   // ← какое именно удалить
    ) {
        // 1. Удалить файл с диска
        fileManager.deleteHistoryAttachment(attachmentToRemove.path)

        // 2. Сформировать новый список БЕЗ удаляемого
        val updatedAttachments = currentAttachments.filterNot {
            it.path == attachmentToRemove.path
        }

        // 3. Обновить блок в тексте истории
        val newBlock = HistoryParser.updateBlockAttachments(
            block = rawBlock,
            attachments = updatedAttachments
        )

        // 4. Заменить в файле
        val content = fileManager.readHistory(serviceKey)
        if (!content.contains(rawBlock)) {
            throw IllegalStateException(
                "Блок записи не найден в истории."
            )
        }
        val updated = content.replaceFirst(rawBlock, newBlock)
        fileManager.saveHistory(serviceKey, updated)
    }
}
