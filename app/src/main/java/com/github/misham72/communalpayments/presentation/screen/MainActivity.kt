package com.github.misham72.communalpayments.presentation.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import com.github.misham72.communalpayments.presentation.screen.screens.main.ControlBetweenScreens
import com.github.misham72.communalpayments.presentation.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(darkTheme = isSystemInDarkTheme(), dynamicColor = false) {
                ControlBetweenScreens()
            }
        }
    }
}