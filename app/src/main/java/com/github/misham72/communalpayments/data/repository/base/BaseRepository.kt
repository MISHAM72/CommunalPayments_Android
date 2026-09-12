package com.github.misham72.communalpayments.data.repository.base

import com.github.misham72.communalpayments.data.common.DataConstants
import com.github.misham72.communalpayments.data.local.file.FileManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class BaseRepository(
    protected val fileManager: FileManager,
    protected val dateFormatPattern: String
) {
    protected fun getCurrentDateTime(): String {
        return SimpleDateFormat(dateFormatPattern, Locale.getDefault()).format(Date())
    }

    protected val headerSeparator: String = "-----------------------------------------------------"
    protected val historyHeader: String = DataConstants.HISTORY_SEPARATOR.repeat(DataConstants.HISTORY_SEPARATOR_COUNT)
}
