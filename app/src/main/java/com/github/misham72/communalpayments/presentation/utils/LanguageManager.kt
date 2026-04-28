package com.github.misham72.communalpayments.presentation.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat

@Suppress("HardcodedStringLiteral")
object LanguageManager {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANG = "selected_language"
    const val DEFAULT_LANG: String = "ru"
    const val ENGLISH_LANG: String = "en"

    fun setLanguage(context: Context, languageCode: String) {
        val locale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locale)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_LANG, languageCode)
        }
    }

    fun applySavedLanguage(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedLang = prefs.getString(KEY_LANG, DEFAULT_LANG) ?: DEFAULT_LANG
        val locale = LocaleListCompat.forLanguageTags(savedLang)
        AppCompatDelegate.setApplicationLocales(locale)
    }
}