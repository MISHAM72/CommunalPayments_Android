package com.github.misham72.communalpayments.domain.userclasses


import com.github.misham72.communalpayments.domain.calculators.PaymentDateCalculator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Troyka(private val calculator: PaymentDateCalculator) {
    data class TroykaData(
        val isHistory: Boolean,
        val previousPayment: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Double,
        val accountNumber: String,
        val startDate: Date?

    )

    fun collectTroykaData(
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        priceTariff: Double,
        accountNumber: String
    ): TroykaData {
        // Используем переданный калькулятор
        val previousDate = calculator.getPreviousPaymentDate(periodMonths, paymentDay, startDate)
        val daysFrom = calculator.getDaysFromPreviousPayment(periodMonths, paymentDay, startDate)
        val daysUntil = calculator.getDaysToNextPayment(periodMonths, paymentDay, startDate)
        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val previousPayment = formatter.format(previousDate)
        val nextPayment = formatter.format(nextDate)

        return TroykaData(
            isHistory = true,
            previousPayment = previousPayment,
            daysFromPayment = daysFrom,
            nextPayment = nextPayment,
            daysUntilPayment = daysUntil,
            priceTariff = priceTariff,
            accountNumber = accountNumber,
            startDate = startDate
        )
    }
}