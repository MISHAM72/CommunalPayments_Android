package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.data.local.FileManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Базовый репозиторий для всех типов платежей
 * Содержит общие методы для работы с датой и файлами
 */
abstract class BaseRepository(
    protected val context: Context,
    protected val fileManager: FileManager
) {
    protected fun getCurrentDateTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())
    }

    protected fun getFileName(tag: String): String {
        return "${tag}_${System.currentTimeMillis()}.txt"
    }

    protected val headerSeparator = "----------------------------------------------------------"
    protected val historyHeader = "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩"
}