package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PaymentDateCalculator
import com.github.misham72.communalpayments.domain.model.ZONTData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ZONT(private val calculator: PaymentDateCalculator) {

    fun collectZONTData(
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        priceTariff: Double,
        accountNumber: String
    ): ZONTData {
        // Используем переданный калькулятор
        val previousDate = calculator.getPreviousPaymentDate(periodMonths, paymentDay, startDate)
        val daysFrom = calculator.getDaysFromPreviousPayment(periodMonths, paymentDay, startDate)
        val daysUntil = calculator.getDaysToNextPayment(periodMonths, paymentDay, startDate)
        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val previousPayment = formatter.format(previousDate)
        val nextPayment = formatter.format(nextDate)

        return ZONTData(
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
