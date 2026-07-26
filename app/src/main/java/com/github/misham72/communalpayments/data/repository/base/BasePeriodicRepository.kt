package com.github.misham72.communalpayments.data.repository.base

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager

abstract class BasePeriodicRepository(
    context: Context, fileManager: FileManager
) : BaseRepository(context, fileManager) {

    // ✅ ЕДИНЫЙ метод для ВСЕХ периодических платежей
    //внутри метода происходит сборка итоговой строки
    @Suppress("SameParameterValue")
    protected fun formatPeriodicPayment(
        accountNumber: String = "",
        dateTime: String,
        serviceName: String,
        nextPayment: String,
        priceTariff: Double,
        isHistory: Boolean,
        periodMonths: Int

    ): String {
        return buildString {
            if (isHistory)
                appendLine(historyHeader)
            appendLine(context.getString(R.string.personal_account_in_text_history, accountNumber))
            appendLine(serviceName)
            appendLine(headerSeparator)
            appendLine(dateTime)
            appendLine(context.getString(R.string.next_payment, nextPayment))
            appendLine(context.getString(R.string.period_months_format, periodMonths))
            appendLine(context.getString(R.string.tariff_card, priceTariff))
            appendLine()
            appendLine(context.getString(R.string.currency_rub, priceTariff))
        }
    }
}
