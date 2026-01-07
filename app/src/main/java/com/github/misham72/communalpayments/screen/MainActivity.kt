package com.github.misham72.communalpayments.screen


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.github.misham72.communalpayments.screen.app.kt.ControlBetweenScreens

import com.github.misham72.communalpayments.ui.theme.AppTheme // ← ИМПОРТИРУЕМ НАШУ ТЕМУ

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(dynamicColor = false) {
        //setContent {
            //AppTheme { // ← ИСПОЛЬЗУЕМ AppTheme вместо CommunalPaymentsTheme
                ControlBetweenScreens()
            // Временная замена для теста

            }
        }
    }
}



