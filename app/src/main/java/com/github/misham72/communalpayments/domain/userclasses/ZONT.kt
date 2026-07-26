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
        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val nextPayment = formatter.format(nextDate)

        return ZONTData(
            isHistory = true,
            nextPayment = nextPayment,
            priceTariff = priceTariff,
            periodMonths = periodMonths,
            accountNumber = accountNumber,
            startDate = startDate
        )
    }
}
