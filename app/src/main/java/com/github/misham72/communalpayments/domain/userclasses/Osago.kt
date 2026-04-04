package com.github.misham72.communalpayments.domain.userclasses


import com.github.misham72.communalpayments.domain.calculators.PaymentDateCalculator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Osago(private val calculator: PaymentDateCalculator) {
    data class OsagoData(
        val isHistory: Boolean,
        val previousPayment: String,
        val daysFromPayment: Long,
        val nextPayment: String,
        val daysUntilPayment: Long,
        val priceTariff: Double,
        val accountNumber: String,
        val startDate: Date?
    )

    fun collectOsagoData(
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        priceTariff: Double,
        accountNumber: String
    ): OsagoData {
        // Используем переданный калькулятор
        val previousDate = calculator.getPreviousPaymentDate(periodMonths, paymentDay, startDate)
        val daysFrom = calculator.getDaysFromPreviousPayment(periodMonths, paymentDay, startDate)
        val daysUntil = calculator.getDaysToNextPayment(periodMonths, paymentDay, startDate)
        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val previousPayment = formatter.format(previousDate)
        val nextPayment = formatter.format(nextDate)

        // 3. СОЗДАНИЕ ОБЪЕКТА С ДАННЫМИ
        return OsagoData(
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