package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PaymentDateCalculator
import com.github.misham72.communalpayments.domain.model.TaxesData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Taxes(private val calculator: PaymentDateCalculator) {
    fun collectTaxesData(
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        priceTariff: Double,
        accountNumber: String
    ): TaxesData {
        // Используем переданный калькулятор
        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val nextPayment = formatter.format(nextDate)

        return TaxesData(
            isHistory = true,
            nextPayment = nextPayment,
            priceTariff = priceTariff,
            periodMonths = periodMonths,
            accountNumber = accountNumber,
            startDate = startDate
        )
    }
}
