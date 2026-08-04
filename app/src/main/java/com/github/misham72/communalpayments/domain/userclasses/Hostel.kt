package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator
import com.github.misham72.communalpayments.domain.model.HostelData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Hostel(private val calculator: PeriodCalculator) {

    fun collectHostelData(
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        priceTariff: Double,
        accountNumber: String
    ): HostelData {
        // Используем переданный калькулятор
        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val nextPayment = formatter.format(nextDate)

        // 3. СОЗДАНИЕ ОБЪЕКТА С ДАННЫМИ
        return HostelData(
            isHistory = true,
            nextPayment = nextPayment,
            priceTariff = priceTariff,
            periodMonths = periodMonths,
            accountNumber = accountNumber,
            startDate = startDate
        )
    }
}
