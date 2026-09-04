package com.github.misham72.communalpayments.presentation.screen.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.PaymentStatus
import com.github.misham72.communalpayments.domain.usecases.GetHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.SaveHistoryUseCase
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.common.UiConstants
import com.github.misham72.communalpayments.presentation.mapper.StatusDisplayMapper
import com.github.misham72.communalpayments.presentation.utils.HISTORY_SEPARATOR
import com.github.misham72.communalpayments.presentation.utils.rememberBoilerSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberButtonBuckSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberCancelButtonSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberCarSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberEditHistoryButtonSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberGarbageSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberGasSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberHostelSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberInternetSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberMTSSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberOsagoSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberSaveButtonSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberTaxesSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberTinkoffSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberWaterSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberlightSoundPlayer
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
//🔴//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun HistoryScreen(
    onBack: () -> Unit,
    initialService: String,
    getHistoryUseCase: GetHistoryUseCase,
    saveHistoryUseCase: SaveHistoryUseCase

) {
    val loadingText = stringResource(R.string.loading)
    var fileContent by remember { mutableStateOf(loadingText) }
    var isEditing by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf(initialService) }
    val errorMessageTemplate = stringResource(R.string.download_error_with_message) // шаблон с %s
    val unknownErrorText = stringResource(R.string.unknown_error) // ← получаем текст ошибки через stringResource
    val buttonBuckSound = rememberButtonBuckSoundPlayer()
    val editHistorySound = rememberEditHistoryButtonSoundPlayer()
    val saveSound = rememberSaveButtonSoundPlayer()
    val cancelSound = rememberCancelButtonSoundPlayer()

    // ↓↓↓ ДОБАВИТЬ ЭТИ 11 ПЛЕЕРОВ ↓↓↓
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

    suspend fun refreshHistory() {
        fileContent = getHistoryUseCase.getHistory(selectedService)
    }
    LaunchedEffect(selectedService) {
        fileContent = loadingText
        fileContent = try {
            getHistoryUseCase.getHistory(selectedService)
        } catch (e: Exception) {
            errorMessageTemplate.format(e.localizedMessage ?: unknownErrorText) // ← используем полученную переменную
        }
    }
    //🔴//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Вертикальная колонка — весь экран
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        //🔴////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //При нажатии вызывает onBack, переданный извне.
        Button(
            onClick = {
                buttonBuckSound?.start()
                onBack()
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.back))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.calculation_history), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        //🔴////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .horizontalScroll(state = rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val services = listOf(
                ServiceKeys.ELECTRICITY to R.string.service_display_name_electricity,
                ServiceKeys.GAS to R.string.service_display_name_gas,
                ServiceKeys.WATER to R.string.service_display_name_water,
                ServiceKeys.GARBAGE to R.string.service_display_name_garbage,
                ServiceKeys.ZONT to R.string.service_display_name_zont,
                ServiceKeys.INTERNET to R.string.service_display_name_internet,
                ServiceKeys.MTS to R.string.service_display_name_mts,
                ServiceKeys.TINKOFF to R.string.service_display_name_tinkoff,
                ServiceKeys.TAXES to R.string.service_display_name_taxes,
                ServiceKeys.TROYKA to R.string.service_display_name_troyka,
                ServiceKeys.OSAGO to R.string.service_display_name_osago,
                ServiceKeys.HOSTEL to R.string.service_display_name_hostel
            ).map { (key, nameRes) -> key to stringResource(nameRes) }

            services.forEach { (key, displayName) ->
                // Определяем звук для каждой услуги
                val sound = when (key) {
                    ServiceKeys.ELECTRICITY -> light
                    ServiceKeys.GAS -> gasSound
                    ServiceKeys.WATER -> waterSound
                    ServiceKeys.GARBAGE -> garbageSound
                    ServiceKeys.ZONT -> boilerSound
                    ServiceKeys.INTERNET -> internetSound
                    ServiceKeys.MTS -> mtsSound
                    ServiceKeys.TINKOFF -> tinkoffSound
                    ServiceKeys.TAXES -> taxesSound
                    ServiceKeys.TROYKA -> carSound
                    ServiceKeys.OSAGO -> osagoSound
                    ServiceKeys.HOSTEL -> hostelSound
                    else -> null
                }

                FilterChip(
                    selected = selectedService == key,
                    onClick = {
                        sound?.start()
                        selectedService = key
                    },
                    label = { Text(displayName) })
            }
        }
        if (isEditing) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)

            ) {
                OutlinedTextField(
                    value = fileContent,
                    onValueChange = { fileContent = it },
                    modifier = Modifier
                        .padding(20.dp)
                        .background(Color.Green.copy(alpha = 0.2f))
                        .fillMaxWidth()
                        .weight(1f),
                    label = { Text(stringResource(R.string.Edit_the_entire_text_To_replace_the_status_in_the_last_entry_click_on_the_button_above)) })
                // КНОПКИ СТАТУСОВ
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentStatus.entries.forEach { paymentStatus ->
                        val displayInfo = StatusDisplayMapper.map(paymentStatus)
                        val statusString = stringResource(displayInfo.emojiResId) + " " + stringResource(displayInfo.textResId)
                        OutlinedButton(
                            onClick = {
                                // ⬇️⬇️⬇️ обработчик нажатия на кнопку статуса
                                fileContent = addStatusToLastRecord(
                                    content = fileContent, newStatus = statusString
                                )
                            }) {
                            Text(
                                text = statusString,
                                color = colorResource(displayInfo.colorResId),
                                fontWeight = FontWeight.Bold  // ← ВОТ ЗДЕСЬ ЖИРНЫЙ ШРИФТ
                            )
                        }
                    }
                }
                val scope = rememberCoroutineScope()
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        cancelSound?.start()
                        scope.launch {
                            refreshHistory()
                            isEditing = false
                        }
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(onClick = {
                        saveSound?.start()
                        scope.launch {
                            saveHistoryUseCase.saveHistory(selectedService, fileContent)
                            isEditing = false
                            refreshHistory()
                        }
                    }) {
                        Text(stringResource(R.string.save))
                    }
                }

            }
        } else {

            val viewScrollState = rememberScrollState()
            Card(
                Modifier
                    .fillMaxWidth()
                    .weight(10f)  // ← занимает всё свободное место

            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Blue.copy(alpha = 0.2f)) // Полупрозрачный
                        .padding(40.dp)
                        .verticalScroll(state = viewScrollState)
                ) {
                    val statusLabel = stringResource(R.string.status_label)
                    val toBePaidLabel = stringResource(R.string.to_be_paid)
                    val serviceDisplayName = when (selectedService) {
                        ServiceKeys.ELECTRICITY -> stringResource(R.string.service_display_name_electricity)
                        ServiceKeys.GAS -> stringResource(R.string.service_display_name_gas)
                        ServiceKeys.WATER -> stringResource(R.string.service_display_name_water)
                        ServiceKeys.GARBAGE -> stringResource(R.string.service_display_name_garbage)
                        ServiceKeys.ZONT -> stringResource(R.string.service_display_name_zont)
                        ServiceKeys.INTERNET -> stringResource(R.string.service_display_name_internet)
                        ServiceKeys.MTS -> stringResource(R.string.service_display_name_mts)
                        ServiceKeys.TINKOFF -> stringResource(R.string.service_display_name_tinkoff)
                        ServiceKeys.TAXES -> stringResource(R.string.service_display_name_taxes)
                        ServiceKeys.TROYKA -> stringResource(R.string.service_display_name_troyka)
                        ServiceKeys.OSAGO -> stringResource(R.string.service_display_name_osago)
                        ServiceKeys.HOSTEL -> stringResource(R.string.service_display_name_hostel)
                        else -> ""
                    }
                    val formattedHistoryText = buildAnnotatedString {
                        val lines = fileContent.replace("<br>", "\n").lines()
                        val dateRegex = Regex(UiConstants.DATE_TIME_REGEX_PATTERN)
                        val outputFormatter = java.time.format.DateTimeFormatter.ofPattern(UiConstants.DATE_OUTPUT_PATTERN, UiConstants.DEFAULT_LOCALE)

                        lines.forEachIndexed { index, line ->
                            val trimmed = line.trim()
                            val isToBePaid = line.contains(toBePaidLabel)
                            val isServiceName = trimmed == serviceDisplayName
                            val dateMatch = dateRegex.find(line)
                            val isDate = dateMatch != null
                            val isOtherLabel = (line.startsWith("( ") && line.endsWith(" )")) ||
                                line.contains(statusLabel)

                            when {
                                isToBePaid -> {
                                    // ✅ Вся строка "К оплате: - 123.45 руб." – красная, крупная, жирная
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Red,
                                            fontSize = 20.sp
                                        )
                                    ) {
                                        append(line)
                                    }
                                }

                                isDate -> {
                                    val dateStr = dateMatch.groupValues[1]
                                    val formattedDate = try {
                                        LocalDate.parse(dateStr).format(outputFormatter)
                                    } catch (_: Exception) {
                                        dateStr
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            fontSize = 20.sp
                                        )
                                    ) {
                                        append(formattedDate)
                                    }
                                }

                                isServiceName -> {
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            fontSize = 20.sp
                                        )
                                    ) {
                                        append(line)
                                    }
                                }

                                isOtherLabel -> {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(line)
                                    }
                                }

                                else -> {
                                    append(line)
                                }
                            }

                            if (index < lines.size - 1) append("\n")
                        }
                    }
                    Text(
                        text = formattedHistoryText, fontSize = 16.sp
                    )
                }
            }
            //🔴///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //  Кнопка «Редактировать историю»
            //Переводит экран в режим редактирования.
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    editHistorySound?.start()
                    isEditing = true
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.Edit_history))
            }
        }
    }
}

//🔴/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun addStatusToLastRecord(content: String, newStatus: String): String {
    val separator = HISTORY_SEPARATOR
    val sepIndex = content.indexOf(separator)
    val before = content.take(sepIndex + separator.length)
    val after = content.drop(sepIndex + separator.length)
    val lines = after.split("\n").toMutableList()

    // Вставляем новый статус
    lines.add(0, newStatus)
    // Собираем обратно
    return before + "\n" + lines.joinToString("\n")
}
