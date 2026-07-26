package com.github.misham72.communalpayments.data.local.preferences

import android.content.Context
import androidx.core.content.edit
import com.github.misham72.communalpayments.R

@Suppress("HardcodedStringLiteral")
class AccountPreferences(context: Context) {

    private companion object {
        private const val KEY_ACCOUNT_PREFIX = "account_"
        private const val KEY_NAME_PREFIX = "name_"
        private const val KEY_DATE_PREFIX = "date_"
    }

    private val prefs = context.getSharedPreferences(
        context.getString(R.string.account_prefs), Context.MODE_PRIVATE
    )

    fun saveAccount(serviceKey: String, number: String) {
        prefs.edit { putString("$KEY_ACCOUNT_PREFIX$serviceKey", number) }
    }

    fun getAccount(serviceKey: String): String {
        return prefs.getString("$KEY_ACCOUNT_PREFIX$serviceKey", "") ?: ""
    }

    fun saveCustomName(serviceKey: String, name: String) {
        prefs.edit { putString("$KEY_NAME_PREFIX$serviceKey", name) }
    }

    fun getCustomName(serviceKey: String): String {
        return prefs.getString("$KEY_NAME_PREFIX$serviceKey", "") ?: ""
    }

    fun saveCustomDate(serviceKey: String, date: String) {
        prefs.edit { putString("$KEY_DATE_PREFIX$serviceKey", date) }
    }

    fun getCustomDate(serviceKey: String): String {
        return prefs.getString("$KEY_DATE_PREFIX$serviceKey", "") ?: ""
    }

    fun saveLastReading(serviceKey: String, value: String) {
        prefs.edit { putString("last_reading_$serviceKey", value) }
    }

    fun getLastReading(serviceKey: String): String {
        return prefs.getString("last_reading_$serviceKey", "") ?: ""
    }

    fun saveTariff(serviceKey: String, tariff: String) {
        prefs.edit { putString("tariff_$serviceKey", tariff) }
    }

    fun getTariff(serviceKey: String): String {
        return prefs.getString("tariff_$serviceKey", "") ?: ""
    }

    fun savePeriodMonths(serviceKey: String, value: String) {
        prefs.edit { putString("PeriodMonths_$serviceKey", value) }
    }

    fun getPeriodMonths(serviceKey: String): String {
        return prefs.getString("PeriodMonths_$serviceKey", "") ?: ""
    }

    fun savePaymentDay(serviceKey: String, value: String) {
        prefs.edit { putString("PaymentDay_$serviceKey", value) }
    }

    fun getPaymentDay(serviceKey: String): String {
        return prefs.getString("PaymentDay_$serviceKey", "") ?: ""
    }

    // Сохранение ИНН
    fun saveInn(serviceKey: String, inn: String) {
        prefs.edit { putString("${serviceKey}_inn", inn) }
    }

    // Получение ИНН
    fun getInn(serviceKey: String): String {
        return prefs.getString("${serviceKey}_inn", "") ?: ""
    }

    //Добавили методы в AccountPreferences для сохранения/загрузки
    // Сохранение навание компании
    fun saveNameCompany(serviceKey: String, nameCompany: String) {
        prefs.edit { putString("${serviceKey}_nameCompany", nameCompany) }
    }

    // Получение навание компании
    fun getNameCompany(serviceKey: String): String {
        return prefs.getString("${serviceKey}_nameCompany", "") ?: ""
    }

    fun saveBankAccount(serviceKey: String, bankAccount: String) {
        prefs.edit { putString("${serviceKey}_bankAccount", bankAccount) }
    }

    // Получение навание компании
    fun getBankAccount(serviceKey: String): String {
        return prefs.getString("${serviceKey}_bankAccount", "") ?: ""
    }

    fun saveWebsiteUrl(serviceKey: String, websiteUrl: String) {
        prefs.edit { putString("${serviceKey}_websiteUrl", websiteUrl) }
    }

    fun getWebsiteUrl(serviceKey: String): String {
        return prefs.getString("${serviceKey}_websiteUrl", "") ?: ""
    }

}
