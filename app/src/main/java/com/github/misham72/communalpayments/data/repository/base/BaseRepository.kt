package com.github.misham72.communalpayments.data.repository.base

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Базовый репозиторий для всех типов платежей.
 * Содержит общие методы для работы с датой и файлами.
 */
abstract class BaseRepository(
    protected val fileManager: FileManager,
    protected val dateFormatPattern: String) {
    protected fun getCurrentDateTime(): String {
        return SimpleDateFormat(dateFormatPattern, Locale.getDefault()).format(Date())
    }

    protected val headerSeparator: String = "-----------------------------------------------------"
    protected val historyHeader: String = "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩"
}
