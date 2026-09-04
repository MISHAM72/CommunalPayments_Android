package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator
import com.github.misham72.communalpayments.domain.common.DomainMessages
import com.github.misham72.communalpayments.domain.model.periodic.PeriodicData
import com.github.misham72.communalpayments.domain.repository.PeriodicRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataCollectorPeriodic(
    private val repository: PeriodicRepository,
    private val settingsRepository: UserSettingsRepository,
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
        require(periodMonths > 0) { DomainMessages.PERIOD_MUST_BE_POSITIVE }
        require(paymentDay in 1..31) { DomainMessages.DAY_OF_PAYMENTS_MUST_BE_FROM_1_TO_31 }

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
        settingsRepository.savePaymentDay(serviceKey, paymentDay.toString())
        settingsRepository.savePeriodMonths(serviceKey, periodMonths.toString())
        settingsRepository.saveTariff(serviceKey, priceTariff.toString())
        settingsRepository.saveLastPeriodicDate(serviceKey, nextPayment)

        return data
    }
}
