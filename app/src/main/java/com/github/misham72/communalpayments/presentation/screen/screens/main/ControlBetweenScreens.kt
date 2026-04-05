package com.github.misham72.communalpayments.presentation.screen.screens.main

import android.app.Activity
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.presentation.screen.components.ServiceTab
import com.github.misham72.communalpayments.presentation.screen.navigation.getListInitialScreen
import com.github.misham72.communalpayments.presentation.screen.screens.history.SimpleHistoryScreen

@Composable
fun ControlBetweenScreens() {
    val context = LocalContext.current
    val accountPrefs = remember { AccountPreferences(context.applicationContext) }
    var selectedService by remember { mutableIntStateOf(0) }
    var showHistory by remember { mutableStateOf(false) }
    val services = getListInitialScreen()
    var dueDates by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showMenu by remember { mutableStateOf(false) }


    // Перезагружаем даты при каждом возобновлении экрана
    LifecycleResumeEffect(Unit) {
        val dates = mutableMapOf<String, String>()
        services.forEach { service ->
            val date = accountPrefs.getCustomDate(service.fileKey)
            if (date.isNotBlank()) {
                dates[service.fileKey] = date
            }
        }
        dueDates = dates
        onPauseOrDispose { }
    }

    fun onNavigateBack() {
        showHistory = false
    }
    if (showHistory) {
        SimpleHistoryScreen(
            onBack = { onNavigateBack() },
            initialService = services[selectedService].fileKey
        )
    } else {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("...")
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Меню")
                        }
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Выйти", fontSize = 20.sp) },
                            onClick = {
                                (context as? Activity)?.finishAffinity()
                                showMenu = false
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    services.forEachIndexed { index, service ->
                        ServiceTab(
                            service = service,
                            isSelected = selectedService == index,
                            dueDate = dueDates[service.fileKey],
                            onClick = { selectedService = index }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    services[selectedService].screen()
                }

                Image(
                    painter = painterResource(R.drawable.night),
                    contentDescription = stringResource(R.string.summer_night),
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

                Button(
                    onClick = { showHistory = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text(stringResource(R.string.history))
                }
            }
        }
    }
}