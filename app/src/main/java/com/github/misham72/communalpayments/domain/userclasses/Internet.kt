package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator

/**
 * Чистый domain класс для интернет-платежей
 * НЕТ Context, НЕТ FileManager, НЕТ сохранения!
 * Только бизнес-логика и данные
 */
class Internet {

    data class InternetData(
        val isHistory: Boolean,
        val previousPayment: String,
        val daysFromPayment: Long,
        val nextPayment: String,
        val daysUntilPayment: Long,
        val priceTariff: Double,
        val accountNumber: String
    )

    fun collectInternetData(
        paymentDay: Int,
        periodMonths: Int,
        priceTariff: Double,
        accountNumber: String
    ): InternetData {
        // Расчет всех дат и сроков
        val previousPayment = PeriodCalculator.getPreviousPaymentString(periodMonths, paymentDay)
        val nextPayment = PeriodCalculator.getNextPaymentString(periodMonths, paymentDay)
        val daysFromPayment = PeriodCalculator.calculateDaysFromPreviousPayment(periodMonths, paymentDay)
        val daysUntilPayment = PeriodCalculator.calculateDaysToNextPayment(periodMonths, paymentDay)

        return InternetData(
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