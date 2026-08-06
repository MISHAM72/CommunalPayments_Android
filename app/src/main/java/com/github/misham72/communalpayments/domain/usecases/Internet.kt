package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator
import com.github.misham72.communalpayments.domain.model.InternetData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Чистый domain класс для интернет-платежей
 * НЕТ Context, НЕТ FileManager, НЕТ сохранения!
 * Только бизнес-логика и данные
 */
class Internet(private val calculator: PeriodCalculator) {
    fun collectInternetData(
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        priceTariff: Double,
        accountNumber: String
    ): InternetData {
        // Используем переданный калькулятор
        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val nextPayment = formatter.format(nextDate)

        return InternetData(
            isHistory = true,
            nextPayment = nextPayment,
            priceTariff = priceTariff,
            periodMonths = periodMonths,
            accountNumber = accountNumber,
            startDate = startDate
        )
    }
}
