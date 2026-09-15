package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.data.local.file.FileManager
import java.io.File

/**
 * Возвращает File для открытия вложения.
 * Возвращает null, если файл не найден.
 */
class GetHistoryAttachmentUseCase(
    private val fileManager: FileManager
) {
    operator fun invoke(path: String): File? {
        return fileManager.getHistoryAttachment(path)
    }
}
