package com.github.misham72.communalpayments.domain.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    fun daysUntil(dateStr: String, format: String = "dd.MM.yyyy"): Int {
        return try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            val dueDate = sdf.parse(dateStr)
            val dueTime = dueDate?.time ?: return Int.MAX_VALUE
            val todayTime = Calendar.getInstance().timeInMillis
            val diff = dueTime - todayTime
            (diff / (24 * 60 * 60 * 1000)).toInt()

        } catch (_: Exception) {
            Int.MAX_VALUE
        }
    }
}
