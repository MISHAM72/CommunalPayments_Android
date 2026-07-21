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
        dateTime: String, serviceName: String, previousPayment: String,     // дата прошлого
        daysFromPayment: Long,       // дней прошло
        nextPayment: String,         // дата следующего
        daysUntilPayment: Long,      // дней осталось
        priceTariff: Double,          // тариф
        isHistory: Boolean, customStatus: String
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
            appendLine(context.getString(R.string.the_payment_was, previousPayment))
            appendLine(context.getString(R.string.passed, daysFromPayment))
            appendLine(context.getString(R.string.next_payment, nextPayment))
            appendLine(context.getString(R.string.payment_in, daysUntilPayment))
            appendLine(context.getString(R.string.tariff_card, priceTariff))
            appendLine(context.getString(R.string.currency_rub, priceTariff))
            // ✅ Добавить пустую строку в конце для красоты
            appendLine()
        }
    }
}
