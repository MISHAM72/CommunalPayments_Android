package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PaymentDateCalculator
import com.github.misham72.communalpayments.domain.model.MTSData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MTS(private val calculator: PaymentDateCalculator) {
    fun collectMTSData(
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        priceTariff: Double,
        accountNumber: String
    ): MTSData {
        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val nextPayment = formatter.format(nextDate)

        return MTSData(
            isHistory = true,
            nextPayment = nextPayment,
            priceTariff = priceTariff,
            periodMonths = periodMonths,
            accountNumber = accountNumber,
            startDate = startDate
        )
    }
}
