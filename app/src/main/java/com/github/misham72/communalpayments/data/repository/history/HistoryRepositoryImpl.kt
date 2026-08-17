package com.github.misham72.communalpayments.data.repository.history

import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.domain.repository.HistoryRepository

class HistoryRepositoryImpl(
    private val fileManager: FileManager
) : HistoryRepository {
    override suspend fun readHistory(serviceKey: String): String {
        return fileManager.readHistory(serviceKey)
    }

    override suspend fun saveHistory(serviceKey: String, content: String) {
        // Сохраняем в файл с тем же именем, что и читаем (например, "serviceKey.txt")
        // Используем существующий метод FileManager.saveToFile, если он есть.
        // Если такого метода нет, добавим его.
        fileManager.saveToFile(content, "$serviceKey.txt")
    }
}
