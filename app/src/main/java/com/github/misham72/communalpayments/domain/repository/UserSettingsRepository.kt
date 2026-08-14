package com.github.misham72.communalpayments.domain.repository

interface UserSettingsRepository {
    suspend fun saveLastReading(serviceKey: String, value: String)
    suspend fun saveTariff(serviceKey: String, tariff: String)
    suspend fun savePaymentDay(serviceKey: String, value: String)
    suspend fun savePeriodMonths(serviceKey: String, value: String)
    suspend fun saveLastPeriodicDate(serviceKey: String, date: String)
    suspend fun saveCustomDate(serviceKey: String, date: String)

    // Чтение (добавить)
    suspend fun getLastReading(serviceKey: String): String?
    suspend fun getTariff(serviceKey: String): String?
    suspend fun getPaymentDay(serviceKey: String): String?
    suspend fun getPeriodMonths(serviceKey: String): String?
    suspend fun getLastPeriodicDate(serviceKey: String): String?
    suspend fun getCustomDate(serviceKey: String): String

}
