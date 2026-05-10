package com.github.misham72.communalpayments.presentation.screen

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import android.content.res.Configuration
import androidx.core.app.ActivityCompat
import com.github.misham72.communalpayments.presentation.screen.screens.main.ControlBetweenScreens
import com.github.misham72.communalpayments.presentation.theme.AppTheme
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.github.misham72.communalpayments.presentation.theme.ThemePrefs
import com.github.misham72.communalpayments.presentation.utils.LanguageManager
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)  // 1. Сначала всегда super

        // 2. Затем все инициализации, использующие Context
        LanguageManager.applySavedLanguage(this)

        val isSystemDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val themeMode = ThemePrefs.getThemeMode(this)
        val useDarkTheme = when (themeMode) {
            ThemePrefs.MODE_LIGHT -> false
            ThemePrefs.MODE_DARK -> true
            else -> isSystemDarkTheme
        }

        // 3. Настройка edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 4. Запрос разрешений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }

        // 5. Установка контента
        setContent {
            AppTheme(darkTheme = useDarkTheme, dynamicColor = false) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    ControlBetweenScreens()
                }
            }
        }
    }
}