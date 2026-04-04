package com.github.misham72.communalpayments.data.local

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
        context.getString(R.string.account_prefs),
        Context.MODE_PRIVATE
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
}