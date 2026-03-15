package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator


class Garbage {

    data class GarbageData(
        val isHistory: Boolean,
        val previousPayment: String,
        val daysFromPayment: Long,
        val nextPayment: String,
        val daysUntilPayment: Long,
        val priceTariff: Double,
        val accountNumber: String
    )

    fun collectGarbageData(
        paymentDay: Int,
        periodMonths: Int,
        priceTariff: Double,
        accountNumber: String
    ): GarbageData {
        // 👇 2. РАСЧЕТ ВСЕХ НЕОБХОДИМЫХ ДАТ И СРОКОВ
        val previousPayment = PeriodCalculator.getPreviousPaymentString(periodMonths, paymentDay)
        val nextPayment = PeriodCalculator.getNextPaymentString(periodMonths, paymentDay)
        val daysFromPayment = PeriodCalculator.calculateDaysFromPreviousPayment(periodMonths, paymentDay)
        val daysUntilPayment = PeriodCalculator.calculateDaysToNextPayment(periodMonths, paymentDay)
        // 3. СОЗДАНИЕ ОБЪЕКТА С ДАННЫМИ
        return GarbageData(
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