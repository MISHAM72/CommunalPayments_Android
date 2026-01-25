package com.github.misham72.communalpayments.presentation.screen.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.presentation.screen.components.ServiceTab
import com.github.misham72.communalpayments.presentation.screen.screens.history.SimpleHistoryScreen
import com.github.misham72.communalpayments.presentation.screen.navigation.getListInitialScreen

@Composable
fun ControlBetweenScreens() {
    var selectedService by remember { mutableIntStateOf(0) }
    var showHistory by remember { mutableStateOf(false) }
    val services = getListInitialScreen()

    if (showHistory) {
        SimpleHistoryScreen(
            onBack = { showHistory = false },

            initialService = services[selectedService].fileKey
        )
    } else {
        // ОБЕРНУЛИ В Surface с цветом из темы
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background  // ← ТЕПЕРЬ ФОН МЕНЯЕТСЯ
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.app_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground  // ← ЦВЕТ ТЕКСТА ИЗ ТЕМЫ
                    )
                }

                // Горизонтальные вкладки с иконками
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    services.forEachIndexed { index, service ->
                        ServiceTab(
                            service = service,
                            isSelected = selectedService == index,
                            onClick = { selectedService = index }
                        )
                    }
                }

                val service = services[selectedService]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        service.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground  // ← ЦВЕТ ТЕКСТА ИЗ ТЕМЫ
                    )
                    if (service.account.isNotEmpty()) {
                        Text(
                            service.account,
                            color = MaterialTheme.colorScheme.error  // ← ВМЕСТО Color.Red
                        )
                    }
                }

                // Экран сервиса
                Box(modifier = Modifier.weight(1f)) {
                    services[selectedService].screen()
                }
                Image(
                    painter = painterResource(R.drawable.night),
                    contentDescription = stringResource(R.string.summer_night),
                    contentScale = ContentScale.FillWidth, // Растягивает по ширине, сохраняя пропорции
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

                // Кнопка с кастомным цветом (если хотите)
                Button(
                    onClick = { showHistory = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                    )
                ) {
                    Text(stringResource(R.string.History))
                }
            }
        }
    }
}