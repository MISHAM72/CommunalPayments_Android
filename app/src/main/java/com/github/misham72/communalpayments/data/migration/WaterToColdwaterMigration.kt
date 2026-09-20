package com.github.misham72.communalpayments.data.migration

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.common.DataConstants
import java.io.File

object WaterToColdwaterMigration {

    private const val FLAG = "migration_water_to_coldwater_v1"
    private const val OLD_KEY = "water"
    private const val NEW_KEY = "coldwater"

    fun migrate(context: Context, prefs: SharedPreferences) {
        // Уже мигрировано — выходим
        if (prefs.getBoolean(FLAG, false)) return

        try {
            // 1. Выбранные услуги
            migrateSelectedServices(prefs)

            // 2. Файл истории
            migrateHistoryFile(context)

            // 3. Настройки
            migratePreferences(prefs)

            // 4. Флаг — только после успеха
            prefs.edit { putBoolean(FLAG, true) }
        } catch (e: Exception) {
            // Если что-то упало — флаг НЕ ставим.
            // При следующем запуске миграция повторится.
            e.printStackTrace()
        }
    }

    private fun migrateSelectedServices(prefs: SharedPreferences) {
        val selected = prefs.getStringSet("selected_services", emptySet()) ?: emptySet()
        if (OLD_KEY in selected) {
            prefs.edit {
                putStringSet("selected_services", (selected - OLD_KEY) + NEW_KEY)
            }
        }
    }

    private fun migrateHistoryFile(context: Context) {
        val historyDir = File(context.filesDir, context.getString(R.string.history))
        val oldFile = File(historyDir, "$OLD_KEY.txt")
        val newFile = File(historyDir, "$NEW_KEY.txt")

        if (oldFile.exists() && !newFile.exists()) {
            oldFile.copyTo(newFile)      // копируем
            oldFile.delete()             // удаляем только после успеха
        }
    }

    private fun migratePreferences(prefs: SharedPreferences) {
        // Префиксы: prefix + "water" → prefix + "coldwater"
        val prefixes = listOf(
            DataConstants.ACCOUNT_PREFIX,
            DataConstants.NAME_PREFIX,
            DataConstants.DATE_PREFIX,
            DataConstants.LAST_READING,
            DataConstants.TARIFF,
            DataConstants.PERIOD_MONTHS,
            DataConstants.PAYMENT_DAY,
            DataConstants.LAST_PERIODIC_DATE,
            DataConstants.LAST_RESULT_PREFIX,
        )
        prefixes.forEach { prefix ->
            copyPref(prefs, prefix + OLD_KEY, prefix + NEW_KEY)
        }

        // Суффиксы: "water" + suffix → "coldwater" + suffix
        val suffixes = listOf(
            DataConstants.INN_SUFFIX,
            DataConstants.NAME_COMPANY_SUFFIX,
            DataConstants.BANK_ACCOUNT_SUFFIX,
            DataConstants.WEBSITE_URL_SUFFIX,
        )
        suffixes.forEach { suffix ->
            copyPref(prefs, OLD_KEY + suffix, NEW_KEY + suffix)
        }
    }

    private fun copyPref(prefs: SharedPreferences, from: String, to: String) {
        if (!prefs.contains(from) || prefs.contains(to)) return

        when (val value = prefs.all[from]) {
            is String -> prefs.edit { putString(to, value) }
            is Int -> prefs.edit { putInt(to, value) }
            is Long -> prefs.edit { putLong(to, value) }
            is Float -> prefs.edit { putFloat(to, value) }
            is Boolean -> prefs.edit { putBoolean(to, value) }
            is Set<*> -> {
                @Suppress("UNCHECKED_CAST")
                prefs.edit { putStringSet(to, value as Set<String>) }
            }
        }
    }
}
