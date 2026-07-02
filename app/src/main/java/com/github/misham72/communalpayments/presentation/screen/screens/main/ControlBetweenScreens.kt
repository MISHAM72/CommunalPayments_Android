package com.github.misham72.communalpayments.presentation.screen.screens.main

import android.app.Activity
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
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.local.IncomeFileManager
import com.github.misham72.communalpayments.data.repository.AnalyticsRepositoryImpl
import com.github.misham72.communalpayments.data.repository.IncomeRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.AddIncomeUseCase
import com.github.misham72.communalpayments.domain.userclasses.GetAllServicesYearlySummaryUseCase
import com.github.misham72.communalpayments.domain.userclasses.GetYearlyIncomeUseCase
import com.github.misham72.communalpayments.domain.userclasses.UpdateIncomeUseCase
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.screen.components.ServiceTab
import com.github.misham72.communalpayments.presentation.screen.navigation.getListInitialScreen
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.AllServicesSummaryScreen
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.AllServicesSummaryViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.IncomeViewModelFactory
import com.github.misham72.communalpayments.presentation.screen.screens.history.SimpleHistoryScreen
import com.github.misham72.communalpayments.presentation.theme.ThemePrefs
import com.github.misham72.communalpayments.presentation.utils.LanguageManager
import com.github.misham72.communalpayments.presentation.utils.PdfHistoryExporter
import com.github.misham72.communalpayments.presentation.utils.rememberBoilerSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberCarSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberGarbageSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberGasSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberHistorySoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberHostelSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberInTotalSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberInternetSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberMTSSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberOsagoSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberTaxesSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberTinkoffSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberWaterSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberlightSoundPlayer
import kotlinx.coroutines.launch

@Composable
fun ControlBetweenScreens(
    onExportBackup: () -> Unit = {},
    onImportBackup: () -> Unit = {}
) {
    val context = LocalContext.current
    val accountPrefs = remember { AccountPreferences(context.applicationContext) }
    var selectedService by remember { mutableIntStateOf(0) }
    val showHistory = remember { mutableStateOf(false) }
    val showAllServicesSummary = remember { mutableStateOf(false) }   // новый флаг
    val services = getListInitialScreen()
    // Зависимости для сводной аналитики по всем услугам
    val fileManager = remember { FileManager(context) }
    val analyticsRepo = remember { AnalyticsRepositoryImpl(fileManager) }
    val getAllServicesUseCase = remember { GetAllServicesYearlySummaryUseCase(analyticsRepo) }
    val defaultError = stringResource(R.string.error_load_default)
    val allServicesFactory = remember { AllServicesSummaryViewModelFactory(getAllServicesUseCase, defaultError) }
    // Зависимости для доходов
    val incomeFileManager = remember { IncomeFileManager(context) }
    val incomeRepo = remember { IncomeRepositoryImpl(incomeFileManager) }
    val getIncomeUseCase = remember { GetYearlyIncomeUseCase(incomeRepo) }
    val addIncomeUseCase = remember { AddIncomeUseCase(incomeRepo) }
    val updateIncomeUseCase = remember { UpdateIncomeUseCase(incomeRepo) }
    val incomeFactory = remember { IncomeViewModelFactory(getIncomeUseCase, addIncomeUseCase, updateIncomeUseCase) }
    var dueDates by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showMenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    val historySound = rememberHistorySoundPlayer()
    val mockingPipeSound = rememberInTotalSoundPlayer()
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
        showHistory.value = false
    }

    if (showAllServicesSummary.value) {
        AllServicesSummaryScreen(
            onBack = { showAllServicesSummary.value = false },
            expensesFactory = allServicesFactory,
            incomeFactory = incomeFactory
        )
    } else if (showHistory.value) {
        SimpleHistoryScreen(
            onBack = { onNavigateBack() },
            initialService = services[selectedService].fileKey
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.app_name),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.menu))
                    }
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.exit), fontSize = 20.sp) },
                        onClick = {
                            (context as? Activity)?.finishAffinity()
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.export_all_pdf_title)) },
                        onClick = {
                            scope.launch {
                                PdfHistoryExporter.exportAllHistoryPdf(context)
                            }
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.language)) },
                        onClick = {
                            showMenu = false
                            showLanguageDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.theme)) },
                        onClick = {
                            showMenu = false
                            showThemeDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.backup_title)) },
                        onClick = {
                            showMenu = false
                            showBackupDialog = true
                        }
                    )
                }

// Диалог резервного копирования
                if (showBackupDialog) {
                    AlertDialog(
                        onDismissRequest = { showBackupDialog = false },
                        title = { Text(stringResource(R.string.backup_title)) },
                        text = {
                            Column {
                                Text(stringResource(R.string.backup_choose_action))
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(onClick = {
                                        showBackupDialog = false
                                        onExportBackup()
                                    }) {
                                        Text(stringResource(R.string.backup_create))
                                    }
                                    Button(onClick = {
                                        showBackupDialog = false
                                        onImportBackup()
                                    }) {
                                        Text(stringResource(R.string.backup_restore))
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showBackupDialog = false }) {
                                Text(stringResource(R.string.close))
                            }
                        }
                    )
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
                if (showThemeDialog) {
                    AlertDialog(
                        onDismissRequest = { showThemeDialog = false },
                        title = { Text(stringResource(R.string.select_theme)) },
                        text = {
                            Column {
                                TextButton(onClick = {
                                    ThemePrefs.setThemeMode(context, ThemePrefs.MODE_SYSTEM)
                                    showThemeDialog = false
                                    (context as? Activity)?.recreate()
                                }) {
                                    Text(stringResource(R.string.system_default))
                                }
                                TextButton(onClick = {
                                    ThemePrefs.setThemeMode(context, ThemePrefs.MODE_LIGHT)
                                    showThemeDialog = false
                                    (context as? Activity)?.recreate()
                                }) {
                                    Text(stringResource(R.string.light))
                                }
                                TextButton(onClick = {
                                    ThemePrefs.setThemeMode(context, ThemePrefs.MODE_DARK)
                                    showThemeDialog = false
                                    (context as? Activity)?.recreate()
                                }) {
                                    Text(stringResource(R.string.dark))
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showThemeDialog = false }) {
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
                val hostelSound = rememberHostelSoundPlayer()


                Row(            //Определение звука для каждой услуги (when). Row с горизонтальным скроллом — чтобы все чипсы поместились.
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    services.forEachIndexed { index, service ->   //services — список всех услуг (получен из getListInitialScreen()).forEachIndexed — для каждой услуги создаётся ServiceTab (кастомный компонент-чипс).
                        val sound = when (service.fileKey) {  // sound — выбирается соответствующий звук для нажатия на чипс (чтобы при переключении играл специфичный звук, если задан).
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
                            ServiceKeys.OSAGO -> osagoSound  // 🚗 → звук ОСАГО
                            ServiceKeys.HOSTEL -> hostelSound
                            else -> null  // Для остальных пока без звука
                        }
                        //Передача звука в кнопку
                        ServiceTab(   // ServiceTab — отображает название услуги, эмодзи/иконку, дату следующего платежа (если есть). По клику меняет selectedService (индекс выбранной вкладки) и запускает звук.
                            service = service,
                            isSelected = selectedService == index,
                            dueDate = dueDates[service.fileKey],
                            onClick = { selectedService = index },
                            onSound = { sound?.start() })    // ← Передаём запуск звука
                    }
                }
                Spacer(modifier = Modifier.height(9.dp))

                Box(modifier = Modifier.weight(1f)) {   // Box — контейнер, в который помещается UI текущей услуги, weight(1f) — заставляет его растянуться на всю высоту внутри Column. Сейчас внутри Box ровно один элемент — результат вызова.
                    services[selectedService].screen()   // — динамически подставляет экран выбранной услуги.
                }
                Image(
                    painter = painterResource(R.drawable.night),
                    contentDescription = stringResource(R.string.summer_night),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            mockingPipeSound?.start()
                            showAllServicesSummary.value = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors()
                    ) {
                        Text(stringResource(R.string.annual_countdown))
                    }
                    Button(
                        onClick = {
                            historySound?.start()
                            showHistory.value = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors()
                    ) {
                        Text(stringResource(R.string.history))
                    }

                }
            }
        }
    }
}
