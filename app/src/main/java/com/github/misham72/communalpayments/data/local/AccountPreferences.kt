package com.github.misham72.communalpayments.data.local

import android.content.Context
import androidx.core.content.edit
import com.github.misham72.communalpayments.R

class AccountPreferences(context: Context) {
    private val prefs = context.getSharedPreferences(context.getString(R.string.account_prefs), Context.MODE_PRIVATE)

    fun saveAccount(serviceKey: String, number: String) {
        prefs.edit { putString("account_$serviceKey", number) }
    }

    fun getAccount(serviceKey: String): String {
        return prefs.getString("account_$serviceKey", "") ?: ""
    }

    // 🔸 НОВЫЙ МЕТОД для сохранения названия услуги
    fun saveCustomName(serviceKey: String, name: String) {
        prefs.edit { putString("name_$serviceKey", name) }
    }

    // 🔸 НОВЫЙ МЕТОД для загрузки названия услуги
    fun getCustomName(serviceKey: String): String {
        return prefs.getString("name_$serviceKey", "") ?: ""
    }
    ///////
    fun saveCustomDate(serviceKey: String, date: String) {
        prefs.edit { putString("date_$serviceKey", date) }
    }

    // 🔸 НОВЫЙ МЕТОД для загрузки названия услуги
    fun getCustomDate(serviceKey: String): String {
        return prefs.getString("date_$serviceKey", "") ?: ""
    }

}