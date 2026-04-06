package com.github.misham72.communalpayments.presentation.screen

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.app.ActivityCompat
import com.github.misham72.communalpayments.presentation.screen.screens.main.ControlBetweenScreens
import com.github.misham72.communalpayments.presentation.theme.AppTheme
import android.Manifest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Запрашиваем разрешение на уведомления для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
        setContent {
            AppTheme(darkTheme = isSystemInDarkTheme(), dynamicColor = false) {
                ControlBetweenScreens()
            }
        }
    }
}