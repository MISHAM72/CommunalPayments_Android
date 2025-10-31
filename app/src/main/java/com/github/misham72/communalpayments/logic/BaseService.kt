package com.github.misham72.communalpayments.logic

import android.content.Context

open class BaseService(
    context: Context, private val serviceType: String, private val unit: String
) {
    protected val fileManager = FileManager(context)

    // Удобный метод-обертка
    fun getCurrentDateTime(): String {
        return fileManager.getCurrentDateTime()
    }

    fun saveCalculationResult(
        current: Double,
        previous: Double,
        tariff: Double,
        consumption: Double,
        payment: Double,
        customStatus: String // ← ДОБАВИЛИ СТАТУС
    ) {
        fileManager.formatMeterReadingPaymentData(
            serviceType = serviceType,
            currentReading = current,
            previousReading = previous,
            consumption = consumption,
            tariff = tariff,
            payment = payment,
            unit = unit,
            formattedDateTime = getCurrentDateTime(),
            customStatus = customStatus // ← ДОБАВЬТЕ ЭТУ СТРОКУ!
        )
    }

}