package com.github.misham72.communalpayments.data.repository.base

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BaseRepository

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
    ): String {
        return buildString {
            if (isHistory) appendLine(historyHeader)
            appendLine(context.getString(R.string.personal_account_in_text_history, accountNumber))
            appendLine(serviceName)
            appendLine(headerSeparator)
            appendLine(dateTime)
            appendLine(headerSeparator)
            appendLine(context.getString(R.string.current_reading, String.format(context.getString(R.string.format_two_decimals), current), context.getString(R.string.unit_kilowatt_hour)))
            appendLine(context.getString(R.string.previous_reading, String.format(context.getString(R.string.format_two_decimals), previous), context.getString(R.string.unit_kilowatt_hour)))
            appendLine(context.getString(R.string.tariff_card, tariff))
            appendLine(context.getString(R.string.consumption, consumption, context.getString(R.string.unit_kilowatt_hour)))
            appendLine(context.getString(R.string.currency_rub, payment))
        }
    }
}
