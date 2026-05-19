package com.github.misham72.communalpayments.presentation.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object ThemePrefs {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"

    // Константы для режимов:
    internal const val MODE_SYSTEM = 0
    internal const val MODE_LIGHT = 1
    internal const val MODE_DARK = 2

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getThemeMode(context: Context): Int {
        return getPrefs(context).getInt(KEY_THEME_MODE, MODE_SYSTEM)
    }

    fun setThemeMode(context: Context, mode: Int) {
        getPrefs(context).edit {
            putInt(KEY_THEME_MODE, mode)
        }
    }
}
