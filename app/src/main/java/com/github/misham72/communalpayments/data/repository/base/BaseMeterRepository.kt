package com.github.misham72.communalpayments.data.repository.base

import com.github.misham72.communalpayments.data.local.file.FileManager

/**
 * Базовый репозиторий для счетчиков (Electricity, Water, Gas)
 */
abstract class BaseMeterRepository(
    fileManager: FileManager,
    dateFormatPattern: String,
    private val personalAccountTemplate: String,      // "Лицевой счёт: %s"
    private val currentReadingTemplate: String,       // "Текущее: %.2f %s"
    private val previousReadingTemplate: String,      // "Предыдущее: %.2f %s"
    private val tariffTemplate: String,               // "Тариф: %.2f"
    private val consumptionTemplate: String,          // "Расход: %.2f %s"
    private val currencyTemplate: String
) : BaseRepository(fileManager, dateFormatPattern) {
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
            appendLine(personalAccountTemplate.format(accountNumber))
            appendLine(serviceName)
            appendLine()
            appendLine(currentReadingTemplate.format(current, unit))
            appendLine(previousReadingTemplate.format(previous, unit))
            appendLine(tariffTemplate.format(tariff))
            appendLine(consumptionTemplate.format(consumption, unit))
            appendLine()
            appendLine(currencyTemplate.format(payment))
        }
    }
}
