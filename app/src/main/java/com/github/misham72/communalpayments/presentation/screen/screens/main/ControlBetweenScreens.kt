package com.github.misham72.communalpayments.presentation.screen.screens.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.utils.LanguageManager
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.screen.components.ServiceTab
import com.github.misham72.communalpayments.presentation.screen.navigation.getListInitialScreen
import com.github.misham72.communalpayments.presentation.screen.screens.history.SimpleHistoryScreen
import com.github.misham72.communalpayments.presentation.utils.rememberBoilerSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberCarSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberGarbageSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberGasSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberHistorySoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberInternetSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberMTSSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberOsagoSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberTaxesSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberTinkoffSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberWaterSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberlightSoundPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ControlBetweenScreens() {
    val context = LocalContext.current
    val accountPrefs = remember { AccountPreferences(context.applicationContext) }
    var selectedService by remember { mutableIntStateOf(0) }
    var showHistory by remember { mutableStateOf(false) }
    val services = getListInitialScreen()
    var dueDates by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showLanguageDialog by remember { mutableStateOf(false) }
    val historySound = rememberHistorySoundPlayer()

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
            onBack = { onNavigateBack() }, initialService = services[selectedService].fileKey
        )
    } else {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.app_title), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("...")
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.menu))
                        }
                    }
                    DropdownMenu(
                        expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.exit), fontSize = 20.sp) }, onClick = {
                            (context as? Activity)?.finishAffinity()
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text(stringResource(R.string.export_history)) }, onClick = {
                            scope.launch {
                                shareHistoryAsync(context)
                            }
                            showMenu = false
                        })
                        DropdownMenuItem(text = { Text(stringResource(R.string.language)) }, onClick = {
                            showMenu = false
                            showLanguageDialog = true
                        })
                    }
                }
                if (showLanguageDialog) {
                    AlertDialog(
                        onDismissRequest = { showLanguageDialog = false },
                        title = { Text(stringResource(R.string.select_language)) },
                        text = {
                            Column {
                                TextButton(onClick = {
                                    LanguageManager.setLanguage(context, LanguageManager.DEFAULT_LANG)
                                    showLanguageDialog = false
                                    (context as? Activity)?.recreate()
                                }) {
                                    Text(stringResource(R.string.russian))
                                }
                                TextButton(onClick = {
                                    LanguageManager.setLanguage(context, LanguageManager.ENGLISH_LANG)
                                    showLanguageDialog = false
                                    (context as? Activity)?.recreate()
                                }) {
                                    Text(stringResource(R.string.english))
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showLanguageDialog = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }
                //Создание плееров (по одному на каждый тип звука)
                val light = rememberlightSoundPlayer()
                val gasSound = rememberGasSoundPlayer()
                val waterSound = rememberWaterSoundPlayer()
                val garbageSound = rememberGarbageSoundPlayer()
                val boilerSound = rememberBoilerSoundPlayer()
                val internetSound = rememberInternetSoundPlayer()
                val mtsSound = rememberMTSSoundPlayer()
                val tinkoffSound = rememberTinkoffSoundPlayer()
                val taxesSound = rememberTaxesSoundPlayer()
                val carSound = rememberCarSoundPlayer()
                val osagoSound = rememberOsagoSoundPlayer()


                Row(            //Определение звука для каждой услуги (when)
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    services.forEachIndexed { index, service ->
                        val sound = when (service.fileKey) {
                            ServiceKeys.ELECTRICITY -> light
                            ServiceKeys.GAS -> gasSound               // 🔥 → звук газа
                            ServiceKeys.WATER -> waterSound            // 💧 → звук воды
                            ServiceKeys.GARBAGE -> garbageSound
                            ServiceKeys.ZONT -> boilerSound
                            ServiceKeys.INTERNET -> internetSound
                            ServiceKeys.MTS -> mtsSound
                            ServiceKeys.TINKOFF -> tinkoffSound
                            ServiceKeys.TAXES -> taxesSound
                            ServiceKeys.TROYKA -> carSound
                            ServiceKeys.OSAGO -> osagoSound             // 🚗 → звук ОСАГО
                            else -> null  // Для остальных пока без звука
                        }
                        //Передача звука в кнопку
                        ServiceTab(
                            service = service,
                            isSelected = selectedService == index,
                            dueDate = dueDates[service.fileKey],
                            onClick = { selectedService = index },
                            onSound = { sound?.start() })    // ← Передаём запуск звука
                    }
                }
                Spacer(modifier = Modifier.height(9.dp))
                Box(modifier = Modifier.weight(1f)) {

                    services[selectedService].screen()
                }
                Image(
                    painter = painterResource(R.drawable.night),
                    contentDescription = stringResource(R.string.summer_night),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f)
                )

                Button(
                    onClick = {
                        historySound?.start()
                        showHistory = true
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors()
                ) {
                    Text(stringResource(R.string.history))
                }
            }
        }
    }
}

@Suppress("HardcodedStringLiteral")
private const val DATE_FORMAT_FILENAME = "yyyyMMdd_HHmmss"
private suspend fun shareHistoryAsync(context: Context) = withContext(Dispatchers.IO) {
    val fileManager = FileManager(context.applicationContext)
    val allHistory = StringBuilder()

    val serviceKeys = listOf(
        ServiceKeys.ELECTRICITY, ServiceKeys.GAS, ServiceKeys.WATER, ServiceKeys.GARBAGE, ServiceKeys.ZONT, ServiceKeys.INTERNET, ServiceKeys.MTS, ServiceKeys.TINKOFF, ServiceKeys.TAXES, ServiceKeys.TROYKA, ServiceKeys.OSAGO
    )

    for (key in serviceKeys) {
        val content = fileManager.readHistory(key)
        if (content.isNotBlank()) {
            allHistory.apply {
                appendLine("=== $key ===")
                appendLine(content)
                appendLine()
            }
        }
    }

    if (allHistory.isEmpty()) return@withContext

    val timeStamp = SimpleDateFormat(DATE_FORMAT_FILENAME, Locale.getDefault()).format(Date())
    val fileName = "payment_history_$timeStamp.txt"
    val file = File(context.cacheDir, fileName)
    file.writeText(allHistory.toString())

    withContext(Dispatchers.Main) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.export_history)))
    }
}