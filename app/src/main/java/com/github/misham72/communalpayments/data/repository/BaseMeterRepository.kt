package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager


/**
 * Базовый репозиторий для счетчиков (Electricity, Water, Gas)
 */
abstract class BaseMeterRepository(
    context: Context, fileManager: FileManager
) : BaseRepository(context, fileManager) {

    protected fun formatMeterPayment(
        dateTime: String,
        serviceName: String,
        current: Double, previous: Double, tariff: Double, consumption: Double, payment: Double, isHistory: Boolean, customStatus: String
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
            appendLine(
                context.getString(
                    R.string.current_reading, String.format(context.getString(R.string.format_two_decimals), current), context.getString(R.string.unit_kilowatt_hour)
                )
            )
            appendLine(
                context.getString(
                    R.string.previous_reading, String.format(context.getString(R.string.format_two_decimals), previous), context.getString(R.string.unit_kilowatt_hour)
                )
            )
            appendLine(
                context.getString(R.string.tariff_card, tariff)
            )
            appendLine(context.getString(R.string.consumption, consumption, context.getString(R.string.unit_kilowatt_hour)))
            appendLine(context.getString(R.string.currency_rub, payment))
        }
    }
}