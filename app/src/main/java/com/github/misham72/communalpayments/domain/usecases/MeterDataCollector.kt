package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.exceptions.InvalidReadingException
import com.github.misham72.communalpayments.domain.model.valueobjects.AccountNumber
import com.github.misham72.communalpayments.domain.model.DomainMessages
import com.github.misham72.communalpayments.domain.model.valueobjects.KilowattHour
import com.github.misham72.communalpayments.domain.model.MeterData
import com.github.misham72.communalpayments.domain.model.valueobjects.Tariff
import com.github.misham72.communalpayments.domain.repository.MeterRepository

class MeterDataCollector(
    private val repository: MeterRepository,
    private val accountPrefs: AccountPreferences
) {
    suspend fun collectMeterData(
        current: Double,
        previous: Double,
        tariff: Double,
        accountNumber: String,
        serviceKey: String,
        factory: (KilowattHour, KilowattHour, Tariff, AccountNumber, Boolean) -> MeterData
    ): MeterData {
        val data = try {
            factory(
                KilowattHour(current),
                KilowattHour(previous),
                Tariff(tariff),
                AccountNumber(accountNumber),
                true
            )
        } catch (e: IllegalArgumentException) {
            throw InvalidReadingException(e.message ?: DomainMessages.DEFAULT_VALIDATION_ERROR)
        }

        repository.save(data)
        accountPrefs.saveLastReading(serviceKey, current.toString())
        accountPrefs.saveTariff(serviceKey, tariff.toString())

        return data
    }
}
