package com.github.misham72.communalpayments.screen


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.github.misham72.communalpayments.screen.app.kt.ControlBetweenScreens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CommunalPaymentsTheme {
                ControlBetweenScreens()
            }
        }
    }
}

@Composable
fun CommunalPaymentsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}


