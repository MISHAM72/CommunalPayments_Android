package com.github.misham72.communalpayments.domain.userclasses


import com.github.misham72.communalpayments.domain.calculators.PaymentDateCalculator
import com.github.misham72.communalpayments.domain.model.OsagoData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Osago(private val calculator: PaymentDateCalculator) {
    fun collectOsagoData(
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        priceTariff: Double,
        accountNumber: String
    ): OsagoData {

        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val nextPayment = formatter.format(nextDate)

        // 3. СОЗДАНИЕ ОБЪЕКТА С ДАННЫМИ
        return OsagoData(
            isHistory = true,
            nextPayment = nextPayment,
            priceTariff = priceTariff,
            periodMonths = periodMonths,
            accountNumber = accountNumber,
            startDate = startDate
        )
    }
}
