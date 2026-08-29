package com.github.misham72.communalpayments.data.local.preferences

import android.content.Context
import androidx.core.content.edit
import com.github.misham72.communalpayments.data.common.DataConstants

@Suppress("HardcodedStringLiteral")
class AccountPreferences(context: Context) {


    private val prefs = context.getSharedPreferences(
        DataConstants.PREFS_NAME, Context.MODE_PRIVATE
    )

    // Ключи для периодических услуг
    fun saveAccount(serviceKey: String, number: String) {
        prefs.edit { putString(DataConstants.ACCOUNT_PREFIX + serviceKey, number) }
    }

    fun getAccount(serviceKey: String): String {
        return prefs.getString(DataConstants.ACCOUNT_PREFIX + serviceKey, "") ?: ""
    }

    fun saveCustomName(serviceKey: String, name: String) {
        prefs.edit { putString(DataConstants.NAME_PREFIX + serviceKey, name) }
    }

    fun getCustomName(serviceKey: String): String {
        return prefs.getString(DataConstants.NAME_PREFIX + serviceKey, "") ?: ""
    }

    fun saveCustomDate(serviceKey: String, date: String) {
        prefs.edit { putString(DataConstants.DATE_PREFIX + serviceKey, date) }
    }

    fun getCustomDate(serviceKey: String): String {
        return prefs.getString(DataConstants.DATE_PREFIX + serviceKey, "") ?: ""
    }

    // Ключи для периодических услуг
    fun saveLastReading(serviceKey: String, value: String) {
        prefs.edit { putString(DataConstants.LAST_READING + serviceKey, value) }
    }

    fun getLastReading(serviceKey: String): String {
        return prefs.getString(DataConstants.LAST_READING + serviceKey, "") ?: ""
    }

    fun saveTariff(serviceKey: String, tariff: String) {
        prefs.edit { putString(DataConstants.TARIFF + serviceKey, tariff) }
    }

    fun getTariff(serviceKey: String): String {
        return prefs.getString(DataConstants.TARIFF + serviceKey, "") ?: ""
    }

    // Ключи для периодических услуг
    fun savePeriodMonths(serviceKey: String, value: String) {
        prefs.edit { putString(DataConstants.PERIOD_MONTHS + serviceKey, value) }
    }

    fun getPeriodMonths(serviceKey: String): String {
        return prefs.getString(DataConstants.PERIOD_MONTHS + serviceKey, "") ?: ""
    }

    fun savePaymentDay(serviceKey: String, value: String) {
        prefs.edit { putString(DataConstants.PAYMENT_DAY + serviceKey, value) }
    }

    fun getPaymentDay(serviceKey: String): String {
        return prefs.getString(DataConstants.PAYMENT_DAY + serviceKey, "") ?: ""
    }

    fun saveLastPeriodicDate(serviceKey: String, date: String) {
        prefs.edit { putString(DataConstants.LAST_PERIODIC_DATE + serviceKey, date) }
    }

    fun getLastPeriodicDate(serviceKey: String): String {
        return prefs.getString(DataConstants.LAST_PERIODIC_DATE + serviceKey, "") ?: ""
    }

    // Ключи для реквизитов провайдера
    fun saveInn(serviceKey: String, inn: String) {
        prefs.edit { putString(serviceKey + DataConstants.INN_SUFFIX, inn) }
    }

    fun getInn(serviceKey: String): String {
        return prefs.getString(serviceKey + DataConstants.INN_SUFFIX, "") ?: ""
    }

    fun saveNameCompany(serviceKey: String, nameCompany: String) {
        prefs.edit { putString(serviceKey + DataConstants.NAME_COMPANY_SUFFIX, nameCompany) }
    }

    fun getNameCompany(serviceKey: String): String {
        return prefs.getString(serviceKey + DataConstants.NAME_COMPANY_SUFFIX, "") ?: ""
    }

    fun saveBankAccount(serviceKey: String, bankAccount: String) {
        prefs.edit { putString(serviceKey + DataConstants.BANK_ACCOUNT_SUFFIX, bankAccount) }
    }

    fun getBankAccount(serviceKey: String): String {
        return prefs.getString(serviceKey + DataConstants.BANK_ACCOUNT_SUFFIX, "") ?: ""
    }

    fun saveWebsiteUrl(serviceKey: String, websiteUrl: String) {
        prefs.edit { putString(serviceKey + DataConstants.WEBSITE_URL_SUFFIX, websiteUrl) }
    }

    fun getWebsiteUrl(serviceKey: String): String {
        return prefs.getString(serviceKey + DataConstants.WEBSITE_URL_SUFFIX, "") ?: ""
    }

    fun saveString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return prefs.getString(key, defaultValue)
    }
}
