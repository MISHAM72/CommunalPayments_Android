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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.model.PaymentStatus
import com.github.misham72.communalpayments.presentation.mapper.StatusDisplayMapper
import kotlinx.coroutines.delay

@Composable
fun SimpleHistoryScreen(
    onBack: () -> Unit, initialService: String
) {
    val context = LocalContext.current
    val loadingText = stringResource(R.string.loading)
    var fileContent by remember { mutableStateOf(loadingText) }
    var isEditing by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf(initialService) }
    val fileManager = remember { FileManager(context) }
    stringResource(R.string.to_be_paid) // "К оплате:"
    stringResource(R.string.date_label)    // "Дата:"
    stringResource(R.string.status_label) // "Статус:"


    fun refreshHistory() {
        fileContent = fileManager.readHistory(selectedService)
    }

    LaunchedEffect(selectedService) {
        fileContent = loadingText
        refreshHistory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {//////////////////////////////////////////////////////////////////////////////////////////////////
        Button(
            onClick = onBack, modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.back))
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Spacer(modifier = Modifier.height(8.dp))

        Text(stringResource(R.string.calculation_history), fontSize = 24.sp, fontWeight = FontWeight.Bold)
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Выбор услуги
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .horizontalScroll(state = rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                stringResource(R.string.service_key_electricity) to stringResource(R.string.service_display_name_electricity),
                stringResource(R.string.service_key_gas) to stringResource(R.string.service_display_name_gas),
                stringResource(R.string.service_key_water) to stringResource(R.string.service_display_name_water),
                stringResource(R.string.service_key_zont) to stringResource(R.string.service_display_name_zont),
                stringResource(R.string.service_key_internet) to stringResource(R.string.service_display_name_internet),
                stringResource(R.string.service_key_mts) to stringResource(R.string.service_display_name_mts),
                stringResource(R.string.service_key_tinkoff) to stringResource(R.string.service_display_name_tinkoff),
                stringResource(R.string.service_key_garbage) to stringResource(R.string.service_display_name_garbage),
                stringResource(R.string.service_key_taxes) to stringResource(R.string.service_display_name_taxes),
                stringResource(R.string.service_key_troyka) to stringResource(R.string.service_display_name_troyka),
                stringResource(R.string.service_key_osago) to stringResource(R.string.service_display_name_osago)
            ).forEach { (service, name) ->
                FilterChip(selected = selectedService == service, onClick = { selectedService = service }, label = { Text(name) })
            }
        }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Редактировать историю
        if (isEditing) {
            val editScrollState = rememberScrollState()
            // Автоскролл при входе в редактирование
            LaunchedEffect(isEditing) {
                if (isEditing) {
                    delay(100) // Ждем отрисовки
                    editScrollState.animateScrollTo(editScrollState.maxValue)
                }
            }
            /////////////////////
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                // Поле для редактирования
                OutlinedTextField(
                    value = fileContent, onValueChange = { fileContent = it }, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), label = {
                        Text(stringResource(R.string.Edit_the_entire_text_To_replace_the_status_in_the_last_entry_click_on_the_button_above))
                    })

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                // 🟢 ⬇️⬇️⬇️ Заголовок и кнопки статусов
                Text(
                    text = stringResource(R.string.Replace_the_status_in_the_LAST_entry), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp)
                        .horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Заменяем presetStatuses.forEach на это:
                    PaymentStatus.entries.forEach { paymentStatus ->
                        // Получаем информацию для отображения из маппера
                        val displayInfo = StatusDisplayMapper.map(paymentStatus)
                        // Собираем строку как раньше: эмодзи + текст
                        val statusString = stringResource(displayInfo.emojiResId) + " " + stringResource(displayInfo.textResId)
                        OutlinedButton(
                            onClick = {
                                // ⬇️⬇️⬇️ обработчик нажатия на кнопку статуса
                                fileContent = addStatusToLastRecord(
                                    content = fileContent, newStatus = statusString
                                )
                            }) {
                            Text(
                                text = statusString, color = colorResource(displayInfo.colorResId), fontWeight = FontWeight.Bold  // ← ВОТ ЗДЕСЬ ЖИРНЫЙ ШРИФТ
                            )
                        }
                    }
                }
                // 🟢 ⬆️⬆️⬆️ Заголовок и кнопки статусов
                ///////////////////////////////////////////////////////////////////////////////////////////////
                // 🔴  ⬇️⬇️⬇️ Кнопки сохранить и отмена
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        fileManager.editFile(selectedService, fileContent)
                        isEditing = false
                        refreshHistory()
                    }) {
                        Text(stringResource(R.string.save))
                    }

                    Button(onClick = {
                        refreshHistory()
                        isEditing = false
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
                // 🔴 ⬆️⬆️⬆️Кнопки сохранить и отмена
            }  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } else {
            // ************************************************** Автоматически скроллим вниз при загрузке
            // РЕЖИМ ПРОСМОТРА
            val viewScrollState = rememberScrollState()
            // Автоскролл вниз при загрузке истории
            /** LaunchedEffect(fileContent) {
            viewScrollState.animateScrollTo(viewScrollState.maxValue)
            }*/
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // *************************************** карточка истории между горизонтальным скроллом и кнопкой редактировать историю.
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(330.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Blue.copy(alpha = 0.2f)) // Полупрозрачный
                        .padding(10.dp)
                        .verticalScroll(state = viewScrollState)
                    // .border(6.dp, Color.Red)  // ← красная рамка вокруг Card
                ) {
                    // stringResource(R.string.custom_ready_service) // Точная часть из strings.xml
                    //stringResource(R.string.date_label)      // "Дата:"
                    val statusLabel = stringResource(R.string.status_label)  // "Статус:"
                    val toBePaidLabel = stringResource(R.string.to_be_paid)  // "К оплате:"

                    val formattedHistoryText = buildAnnotatedString {
                        // Разбиваем содержимое файла на строки
                        val lines = fileContent.replace("<br>", "\n").lines()

                        lines.forEachIndexed { index, line ->
                            // Проверяем, содержит ли строка ключевые метки
                            val isLabelLine = line.startsWith("Услуга -") ||  // Вместо line.contains(serviceLabelBase)
                                    (line.startsWith("( ") && line.endsWith(" )")) ||  // ← ВОТ И ВСЁ!
                                    // line.contains("Дата:") ||
                                    line.contains(statusLabel) || line.contains(toBePaidLabel)
                            if (isLabelLine) {
                                // Строки с метками делаем ЖИРНЫМИ
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(line)
                                }
                            } else {
                                // Остальные строки - обычным шрифтом
                                append(line)
                            }

                            // Добавляем перенос строки (кроме последней)
                            if (index < lines.size - 1) {
                                append("\n")
                            }
                        }
                    }

                    Text(
                        text = formattedHistoryText, fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            Button(
                onClick = {
                    isEditing = true
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.Edit_history))
            }
        }
    }
}

private val DEFAULT_SEPARATOR = "🟩".repeat(14)
fun addStatusToLastRecord(content: String, newStatus: String): String {
    val separator = DEFAULT_SEPARATOR
    val sepIndex = content.indexOf(separator)

    val before = content.substring(0, sepIndex + separator.length)
    val after = content.substring(sepIndex + separator.length)

    val lines = after.split("\n").toMutableList()

    // Вставляем новый статус
    lines.add(0, newStatus)

    // Собираем обратно
   // return before + lines.joinToString("\n")
    return before + "\n" + lines.joinToString("\n")
}