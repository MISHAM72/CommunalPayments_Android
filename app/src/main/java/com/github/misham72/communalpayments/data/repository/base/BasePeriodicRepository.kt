package com.github.misham72.communalpayments.data.repository.base

import com.github.misham72.communalpayments.data.local.file.FileManager

abstract class BasePeriodicRepository(
    fileManager: FileManager,
    dateFormatPattern: String,
    private val personalAccountTemplate: String,
    private val nextPaymentTemplate: String,
    private val priceTariffTemplate: String,
    private val periodMonthsTemplate: String,
    private val currencyTemplate: String
) : BaseRepository(fileManager, dateFormatPattern) {

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
            appendLine(dateTime)
            appendLine(headerSeparator)
            appendLine(personalAccountTemplate.format(accountNumber))
            appendLine(serviceName)
            appendLine()
            appendLine(nextPaymentTemplate.format(nextPayment))
            appendLine(periodMonthsTemplate.format(periodMonths))
            appendLine(priceTariffTemplate.format(priceTariff))
            appendLine()
            appendLine(currencyTemplate.format(priceTariff))
        }
    }
}
