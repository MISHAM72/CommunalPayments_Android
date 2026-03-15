package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator

class ZONT {

    data class ZONTData(
        val isHistory: Boolean, val previousPayment: String, val daysFromPayment: Long, val nextPayment: String, val daysUntilPayment: Long, val priceTariff: Double, val accountNumber: String
    )

    fun collectZONTData(
        paymentDay: Int, periodMonths: Int, priceTariff: Double, accountNumber: String
    ): ZONTData {
        // 👇 2. РАСЧЕТ ВСЕХ НЕОБХОДИМЫХ ДАТ И СРОКОВ
        val previousPayment = PeriodCalculator.getPreviousPaymentString(monthsPeriod = periodMonths, paymentDay = paymentDay)
        val daysFromPayment = PeriodCalculator.calculateDaysFromPreviousPayment(periodMonths, paymentDay)
        val nextPayment = PeriodCalculator.getNextPaymentString(periodMonths, paymentDay)
        val daysUntilPayment = PeriodCalculator.calculateDaysToNextPayment(periodMonths, paymentDay)

        return ZONTData(
            isHistory = true, previousPayment = previousPayment, nextPayment = nextPayment, daysFromPayment = daysFromPayment, daysUntilPayment = daysUntilPayment, priceTariff = priceTariff, accountNumber = accountNumber
        )
    }
}
