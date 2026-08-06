package com.github.misham72.communalpayments.data.repository.base

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager

/**
 * Базовый репозиторий для счетчиков (Electricity, Water, Gas)
 */
abstract class BaseMeterRepository(
    context: Context, fileManager: FileManager
) : BaseRepository(context, fileManager) {
    @Suppress("SameParameterValue")
    protected fun formatMeterPayment(
        accountNumber: String = "",
        dateTime: String,
        serviceName: String,
        current: Double,
        previous: Double,
        tariff: Double,
        consumption: Double,
        payment: Double,
        isHistory: Boolean,
        unit: String
    ): String {
        return buildString {
            if (isHistory) appendLine(historyHeader)
            appendLine(dateTime)
            appendLine(headerSeparator)
            appendLine(context.getString(R.string.personal_account_in_text_history, accountNumber))
            appendLine(serviceName)
            appendLine()
            appendLine(context.getString(R.string.current_reading, current, unit))
            appendLine(context.getString(R.string.previous_reading, previous, unit))
            appendLine(context.getString(R.string.tariff_card, tariff))
            appendLine(context.getString(R.string.consumption, consumption, unit))
            appendLine()
            appendLine(context.getString(R.string.currency_rub, payment))
        }
    }
}
