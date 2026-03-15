package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator

class Tinkoff {
    data class TinkoffData(   // - отвечает за хранение структуры данных
        val isHistory: Boolean,
        val previousPayment: String,
        val daysFromPayment: Long,
        val nextPayment: String,
        val daysUntilPayment: Long,
        val priceTariff: Double,
        val accountNumber: String
    )

    fun collectTinkoffData(   // — отвечает только за расчет данных
        paymentDay: Int,
        periodMonths: Int,
        priceTariff: Double,
        accountNumber: String
    ): TinkoffData {
        val previousPayment = PeriodCalculator.getPreviousPaymentString(monthsPeriod = periodMonths, paymentDay = paymentDay)
        val daysFromPayment = PeriodCalculator.calculateDaysFromPreviousPayment(periodMonths, paymentDay)
        val nextPayment = PeriodCalculator.getNextPaymentString(periodMonths, paymentDay)
        val daysUntilPayment = PeriodCalculator.calculateDaysToNextPayment(periodMonths, paymentDay)

        return TinkoffData(
            isHistory = true,
            previousPayment = previousPayment,
            daysFromPayment = daysFromPayment,
            nextPayment = nextPayment,
            daysUntilPayment = daysUntilPayment,
            priceTariff = priceTariff,
            accountNumber = accountNumber
        )
    }
}