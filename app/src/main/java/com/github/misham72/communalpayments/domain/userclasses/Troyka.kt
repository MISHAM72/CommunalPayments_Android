package com.github.misham72.communalpayments.domain.userclasses


import com.github.misham72.communalpayments.domain.calculators.PaymentDateCalculator
import com.github.misham72.communalpayments.domain.model.TroykaData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Troyka(private val calculator: PaymentDateCalculator) {

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
            periodMonths = periodMonths.toString(),
            accountNumber = accountNumber,
            startDate = startDate
        )
    }
}
