package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator

class Taxes {
    data class TaxesData(
        val isHistory: Boolean, val previousPayment: String, val daysFromPayment: Long, val nextPayment: String, val daysUntilPayment: Long, val priceTariff: Double, val accountNumber: String
    )


    fun collectTaxesData(
        paymentDay: Int, periodMonths: Int, priceTariff: Double, accountNumber: String
    ): TaxesData {
        // 👇 2. РАСЧЕТ ВСЕХ НЕОБХОДИМЫХ ДАТ И СРОКОВ
        val previousPayment = PeriodCalculator.getPreviousPaymentString(monthsPeriod = periodMonths, paymentDay = paymentDay)
        val daysFromPayment = PeriodCalculator.calculateDaysFromPreviousPayment(periodMonths, paymentDay)
        val nextPayment = PeriodCalculator.getNextPaymentString(periodMonths, paymentDay)
        val daysUntilPayment = PeriodCalculator.calculateDaysToNextPayment(periodMonths, paymentDay)

        return TaxesData(
            isHistory = true, previousPayment = previousPayment, daysFromPayment = daysFromPayment, nextPayment = nextPayment, daysUntilPayment = daysUntilPayment, priceTariff = priceTariff, accountNumber = accountNumber
        )
    }
}