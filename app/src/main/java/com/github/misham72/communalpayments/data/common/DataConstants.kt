package com.github.misham72.communalpayments.data.common

@Suppress("HardcodedStringLiteral")
object DataConstants {
    // Из FileConstants.kt
    const val INCOME_HISTORY_DIR = "income_history"
// при необходимости можно добавить другие:
// const val BACKUP_DIR = "backup"
// const val EXPORT_DIR = "exports"

    // Из PrefKeys.kt
    const val PREFS_NAME = "Настройки учетной записи"

    // Префиксы для основных настроек
    const val ACCOUNT_PREFIX = "account_"
    const val NAME_PREFIX = "name_"
    const val DATE_PREFIX = "date_"

    // Ключи для последних показаний и тарифов (метрики)
    const val LAST_READING = "last_reading_"
    const val TARIFF = "tariff_"

    // Ключи для периодических услуг
    const val PERIOD_MONTHS = "PeriodMonths_"
    const val PAYMENT_DAY = "PaymentDay_"
    const val LAST_PERIODIC_DATE = "last_periodic_date_"

    // Ключи для реквизитов провайдера
    const val INN_SUFFIX = "_inn"
    const val NAME_COMPANY_SUFFIX = "_nameCompany"
    const val BANK_ACCOUNT_SUFFIX = "_bankAccount"
    const val WEBSITE_URL_SUFFIX = "_websiteUrl"

    // Форматы и имена файлов
    const val DATE_FORMAT_FILENAME = "yyyyMMdd_HHmmss"

    // Форматы данных
    const val AMOUNT_FORMAT = "%.2f"
    const val INCOME_RECORD_DATE_PATTERN = "yyyy-MM-dd"

}
