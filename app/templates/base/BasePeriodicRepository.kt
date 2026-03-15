package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager

/**
 * Базовый репозиторий для периодических платежей (Internet, MTS, Taxes)
 */
abstract class BasePeriodicRepository(
    context: Context,
    fileManager: FileManager
) : BaseRepository(context, fileManager) {

    protected fun formatPeriodicPayment(
        dateTime: String,
        serviceName: String,
        payment: Double,
        isHistory: Boolean,
        customStatus: String
    ): String {
        return buildString {
            if (isHistory) appendLine(historyHeader)

            appendLine(serviceName)
            appendLine(headerSeparator)
            appendLine(dateTime)

            if (customStatus.isNotEmpty()) {
                appendLine(customStatus)
            }

            appendLine(headerSeparator)
            appendLine(context.getString(R.string.currency_rub, payment))
        }
    }
}