package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator

class Troyka {
    data class TroykaData(
        val isHistory: Boolean,
        val previousPayment: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Double,
        val accountNumber: String

    )

    fun collectTroykaData(
        paymentDay: Int,
        periodMonths: Int,
        priceTariff: Double,
        accountNumber: String
    ): TroykaData {
        val previousPayment = PeriodCalculator.getPreviousPaymentString(monthsPeriod = periodMonths, paymentDay = paymentDay)
        val daysFromPayment = PeriodCalculator.calculateDaysFromPreviousPayment(periodMonths, paymentDay)
        val nextPayment = PeriodCalculator.getNextPaymentString(periodMonths, paymentDay)
        val daysUntilPayment = PeriodCalculator.calculateDaysToNextPayment(periodMonths, paymentDay)

        return TroykaData(
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