package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager

/**
 * Базовый репозиторий для счетчиков (Electricity, Water, Gas)
 */
abstract class BaseMeterRepository(
    context: Context,
    fileManager: FileManager
) : BaseRepository(context, fileManager) {

    protected fun formatMeterPayment(
        dateTime: String,
        serviceName: String,
        current: Double,
        previous: Double,
        tariff: Double,
        consumption: Double,
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
            appendLine(
                context.getString(
                    R.string.current_reading,
                    String.format("%.2f", current), "кВт"
                )
            )
            appendLine(
                context.getString(
                    R.string.previous_reading,
                    String.format("%.2f", previous), "кВт"
                )
            )
            appendLine(
                context.getString(
                    R.string.tariff_card,
                    String.format("%.2f", tariff)
                )
            )
            appendLine(context.getString(R.string.consumption, consumption, "кВт"))
            appendLine(context.getString(R.string.currency_rub, payment))
        }
    }
}
