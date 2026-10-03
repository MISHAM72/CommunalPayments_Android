package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator
import com.github.misham72.communalpayments.domain.common.DomainMessages
import com.github.misham72.communalpayments.domain.constants.ServiceKeys
import com.github.misham72.communalpayments.domain.model.periodic.GarbageData
import com.github.misham72.communalpayments.domain.model.periodic.GarbageMode
import com.github.misham72.communalpayments.domain.repository.GarbageRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GarbageDataUseCase(
    private val repository: GarbageRepository,   // ← см. пункт 4
    private val settingsRepository: UserSettingsRepository,
    private val calculator: PeriodCalculator
) {
    suspend fun collectGarbageData(
        isHistory: Boolean,
        paymentDay: Int,
        periodMonths: Int,
        startDate: Date,
        mode: GarbageMode,
        value: Double,        // площадь или кол-во
        priceTariff: Double,  // тариф за 1 единицу
        accountNumber: String
    ): GarbageData {
        require(priceTariff > 0.0) { DomainMessages.TARIFF_MUST_BE_POSITIVE }
        require(periodMonths > 0) { DomainMessages.PERIOD_MUST_BE_POSITIVE }
        require(paymentDay in 1..31) { DomainMessages.DAY_OF_PAYMENTS_MUST_BE_FROM_1_TO_31 }
        require(value > 0.0) { DomainMessages.VALUE_MUST_BE_POSITIVE }

        val totalAmount = priceTariff * value * periodMonths

        val nextDate = calculator.getNextPaymentDate(periodMonths, paymentDay, startDate)
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val nextPayment = formatter.format(nextDate)

        val data = GarbageData(
            serviceKey = ServiceKeys.GARBAGE,
            isHistory = isHistory,
            nextPayment = nextPayment,
            mode = mode,
            value = value,
            priceTariff = priceTariff,
            totalAmount = totalAmount,
            periodMonths = periodMonths,
            accountNumber = accountNumber,
            startDate = startDate
        )

        repository.save(data)

        settingsRepository.savePaymentDay(ServiceKeys.GARBAGE, paymentDay.toString())
        settingsRepository.savePeriodMonths(ServiceKeys.GARBAGE, periodMonths.toString())
        settingsRepository.saveTariff(ServiceKeys.GARBAGE, priceTariff.toString())
        settingsRepository.saveLastPeriodicDate(ServiceKeys.GARBAGE, nextPayment)

        return data
    }
}
