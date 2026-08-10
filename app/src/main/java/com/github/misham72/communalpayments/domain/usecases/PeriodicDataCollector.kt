package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator
import com.github.misham72.communalpayments.domain.common.DomainMessages
import com.github.misham72.communalpayments.domain.model.periodic.PeriodicData
import com.github.misham72.communalpayments.domain.repository.PeriodicRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PeriodicDataCollector(
    private val repository: PeriodicRepository,
    private val accountPrefs: AccountPreferences,
    private val calculator: PeriodCalculator
) {
    suspend fun collectPeriodicData(
        serviceKey: String,
        isHistory: Boolean,
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        priceTariff: Double,
        accountNumber: String
    ): PeriodicData {
        // Валидация
        require(priceTariff > 0.0) { DomainMessages.TARIFF_MUST_BE_POSITIVE }
        require(periodMonths > 0) { "Период должен быть больше нуля" }
        require(paymentDay in 1..31) { "День платежа должен быть от 1 до 31" }

        // Вычисляем дату следующего платежа
        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val nextPayment = formatter.format(nextDate)

        val data = PeriodicData(
            serviceKey = serviceKey,
            isHistory = isHistory,
            nextPayment = nextPayment,
            priceTariff = priceTariff,
            periodMonths = periodMonths,
            accountNumber = accountNumber,
            startDate = startDate
        )

        repository.save(data)

        // Сохраняем в Preferences
        accountPrefs.savePaymentDay(serviceKey, paymentDay.toString())
        accountPrefs.savePeriodMonths(serviceKey, periodMonths.toString())
        accountPrefs.saveTariff(serviceKey, priceTariff.toString())
        accountPrefs.saveLastPeriodicDate(serviceKey, nextPayment)

        return data
    }
}
