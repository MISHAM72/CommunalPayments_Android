package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Базовый репозиторий для всех типов платежей
 * Содержит общие методы для работы с датой и файлами
 */
abstract class BaseRepository(
    protected val context: Context, protected val fileManager: FileManager
) {
    protected fun getCurrentDateTime(): String {
        return SimpleDateFormat(context.getString(R.string.yyyy_mm_dd_hh_mm_ss), Locale.getDefault()).format(Date())
    }

    protected val headerSeparator: String = "----------------------------------------------------------"
    protected val historyHeader: String = "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩"
}