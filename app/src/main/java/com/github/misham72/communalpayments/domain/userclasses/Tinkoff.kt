package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator
import com.github.misham72.communalpayments.domain.model.TinkoffData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Tinkoff(private val calculator: PeriodCalculator) {
    fun collectTinkoffData(   // — отвечает только за расчет данных
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        priceTariff: Double,
        accountNumber: String
    ): TinkoffData {

        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val nextPayment = formatter.format(nextDate)

        return TinkoffData(
            isHistory = true,
            nextPayment = nextPayment,
            priceTariff = priceTariff,
            periodMonths = periodMonths,
            accountNumber = accountNumber,
            startDate = startDate
        )
    }
}
