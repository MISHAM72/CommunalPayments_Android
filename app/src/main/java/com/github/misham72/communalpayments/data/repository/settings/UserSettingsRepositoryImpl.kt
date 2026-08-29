package com.github.misham72.communalpayments.data.repository.settings

import com.github.misham72.communalpayments.data.common.DataConstants
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserSettingsRepositoryImpl(
    private val accountPrefs: AccountPreferences
) : UserSettingsRepository {
    override suspend fun saveLastReading(serviceKey: String, value: String) {
        accountPrefs.saveLastReading(serviceKey, value)
    }

    override suspend fun getLastReading(serviceKey: String): String {
        return accountPrefs.getLastReading(serviceKey)
    }

    override suspend fun saveTariff(serviceKey: String, tariff: String) {
        accountPrefs.saveTariff(serviceKey, tariff)
    }

    override suspend fun getTariff(serviceKey: String): String {
        return accountPrefs.getTariff(serviceKey)
    }

    override suspend fun savePaymentDay(serviceKey: String, value: String) {
        accountPrefs.savePaymentDay(serviceKey, value)
    }

    override suspend fun getPaymentDay(serviceKey: String): String {
        return accountPrefs.getPaymentDay(serviceKey)
    }

    override suspend fun savePeriodMonths(serviceKey: String, value: String) {
        accountPrefs.savePeriodMonths(serviceKey, value)
    }

    override suspend fun getPeriodMonths(serviceKey: String): String {
        return accountPrefs.getPeriodMonths(serviceKey)
    }

    override suspend fun saveLastPeriodicDate(serviceKey: String, date: String) {
        accountPrefs.saveLastPeriodicDate(serviceKey, date)
    }

    override suspend fun getLastPeriodicDate(serviceKey: String): String {
        return accountPrefs.getLastPeriodicDate(serviceKey)
    }

    override suspend fun saveCustomDate(serviceKey: String, date: String) {
        accountPrefs.saveCustomDate(serviceKey, date)
    }

    override suspend fun getCustomDate(serviceKey: String): String {
        return accountPrefs.getCustomDate(serviceKey)
    }

    override suspend fun saveLastResult(serviceKey: String, result: String) {
        withContext(Dispatchers.IO) {
            accountPrefs.saveString(DataConstants.LAST_RESULT_PREFIX + serviceKey, result)
        }
    }

    override suspend fun getLastResult(serviceKey: String): String? {
        return withContext(Dispatchers.IO) {
            accountPrefs.getString(DataConstants.LAST_RESULT_PREFIX + serviceKey)
        }
    }
}
