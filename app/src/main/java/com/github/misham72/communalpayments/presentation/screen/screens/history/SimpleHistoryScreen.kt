package com.github.misham72.communalpayments.presentation.screen.screens.history

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager

// Список статусов
val presetStatuses = listOf(
    "🔴 ОПЛАЧЕНО", "🟡 ОЖИДАЕТ ОПЛАТЫ", "🟢 АКТИВНО", "🔵 НА ПРОВЕРКЕ", "⚫ ОТМЕНЕНО"
)

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

    // Получаем строки из ресурсов
    val paymentLabel = stringResource(R.string.to_be_paid) // "К оплате:"
    val dateLabel = stringResource(R.string.date_label)    // "Дата:"
    val statusLabel = stringResource(R.string.status_label) // "Статус:"

    fun loadHistoryData() {
        fileContent = fileManager.loadFromFile(selectedService)
    }

    LaunchedEffect(selectedService) {
        fileContent = loadingText
        loadHistoryData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.back))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(stringResource(R.string.calculation_history), fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Выбор услуги
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)
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

        if (isEditing) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // ★★★ КНОПКИ ДЛЯ ЗАМЕНЫ СТАТУСА В ПОСЛЕДНЕЙ ЗАПИСИ ★★★
                Text(
                    text = stringResource(R.string.Replace_the_status_in_the_LAST_entry), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetStatuses.forEach { status ->
                        OutlinedButton(
                            onClick = {
                                // ★★★ ФУНКЦИЯ ДЛЯ РЕДАКТИРОВАНИЯ ТОЛЬКО ПОСЛЕДНЕЙ ЗАПИСИ ★★★
                                fileContent = replaceStatusInLastRecord(
                                    content = fileContent, newStatus = status, paymentLabel = paymentLabel, dateLabel = dateLabel, statusLabel = statusLabel
                                )
                            }, modifier = Modifier
                        ) {
                            Text(status)
                        }
                    }
                }

                // Кнопка для удаления статуса из последней записи
                OutlinedButton(
                    onClick = {
                        // Удаляем статус только из последней записи
                        fileContent = removeStatusFromLastRecord(
                            content = fileContent, statusLabel = statusLabel
                        )
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(stringResource(R.string.Delete_the_status_from_the_last_entry))
                }

                // Поле для редактирования
                OutlinedTextField(
                    value = fileContent,
                    onValueChange = { fileContent = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    maxLines = Int.MAX_VALUE,
                    label = { Text(stringResource(R.string.Edit_the_entire_text_To_replace_the_status_in_the_last_entry_click_on_the_button_above)) })

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        fileManager.editFile(selectedService, fileContent)
                        isEditing = false
                        loadHistoryData()
                    }) {
                        Text(stringResource(R.string.save))
                    }

                    Button(onClick = {
                        loadHistoryData()
                        isEditing = false
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            }
        } else {
            Column {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = fileContent.replace("<br>", "\n"), fontSize = 16.sp
                        )
                    }
                }
                Button(
                    onClick = { isEditing = true }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.Edit_history))
                }
            }
        }
    }
}

// ★★★ ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ДЛЯ РАБОТЫ С ПОСЛЕДНЕЙ ЗАПИСЬЮ ★★★

// Разделитель по умолчанию - 12 квадратиков
val DEFAULT_SEPARATOR = "🟩".repeat(12)

/**
 * Находит последнюю запись и заменяет/добавляет в ней статус
 */
fun replaceStatusInLastRecord(
    content: String, newStatus: String, paymentLabel: String,  // "К оплате:"
    dateLabel: String,     // "Дата:"
    statusLabel: String    // "Статус:"
): String {
    // Простой способ: ищем последнее вхождение разделителя
    val lastIndex = content.lastIndexOf(DEFAULT_SEPARATOR)

    return if (lastIndex != -1) {
        // Есть разделитель - обрабатываем последнюю запись
        val beforeSeparator = content.substring(0, lastIndex).trim()
        val afterSeparator = content.substring(lastIndex + DEFAULT_SEPARATOR.length).trim()

        val updatedLastRecord = replaceOrAddStatusInRecord(
            record = afterSeparator, newStatus = newStatus, paymentLabel = paymentLabel, dateLabel = dateLabel, statusLabel = statusLabel
        )

        "$beforeSeparator\n\n$DEFAULT_SEPARATOR\n\n$updatedLastRecord"
    } else {
        // Разделителя нет - значит это одна запись
        replaceOrAddStatusInRecord(
            record = content, newStatus = newStatus, paymentLabel = paymentLabel, dateLabel = dateLabel, statusLabel = statusLabel
        )
    }
}

/**
 * Находит последнюю запись и удаляет из нее строку статуса
 */
fun removeStatusFromLastRecord(
    content: String, statusLabel: String    // "Статус:"
): String {
    // Простой способ: ищем последнее вхождение разделителя
    val lastIndex = content.lastIndexOf(DEFAULT_SEPARATOR)

    return if (lastIndex != -1) {
        val beforeSeparator = content.substring(0, lastIndex).trim()
        val afterSeparator = content.substring(lastIndex + DEFAULT_SEPARATOR.length).trim()

        val updatedLastRecord = removeStatusFromRecord(
            record = afterSeparator, statusLabel = statusLabel
        )

        "$beforeSeparator\n\n$DEFAULT_SEPARATOR\n\n$updatedLastRecord"
    } else {
        removeStatusFromRecord(
            record = content, statusLabel = statusLabel
        )
    }
}

/**
 * Заменяет существующий статус или добавляет новый в одной записи
 */
fun replaceOrAddStatusInRecord(
    record: String, newStatus: String, paymentLabel: String,  // "К оплате:"
    dateLabel: String,     // "Дата:"
    statusLabel: String    // "Статус:"
): String {
    // Паттерн для поиска строки со статусом
    val statusPattern = Regex("$statusLabel.*")

    val lines = record.lines().toMutableList()

    // Ищем строку со статусом
    val statusIndex = lines.indexOfFirst { statusPattern.matches(it.trim()) }

    if (statusIndex != -1) {
        // Если есть строка статуса - заменяем её
        lines[statusIndex] = "$statusLabel $newStatus"
    } else {
        // Если нет строки статуса - добавляем в нужное место
        addStatusToRecord(
            lines = lines, newStatus = newStatus, paymentLabel = paymentLabel, dateLabel = dateLabel, statusLabel = statusLabel
        )
    }

    return lines.joinToString("\n")
}

/**
 * Добавляет статус в запись в нужное место
 */
fun addStatusToRecord(
    lines: MutableList<String>, newStatus: String, paymentLabel: String,  // "К оплате:"
    dateLabel: String,     // "Дата:"
    statusLabel: String    // "Статус:"
) {
    // Ищем строку с "К оплате:" (обычно перед статусом)
    val paymentIndex = lines.indexOfFirst { it.contains(paymentLabel) }

    if (paymentIndex != -1) {
        // Вставляем после строки с оплатой
        lines.add(paymentIndex + 1, "$statusLabel $newStatus")
    } else {
        // Ищем строку с датой (формат "Дата: ...")
        val dateIndex = lines.indexOfFirst { it.contains(dateLabel) }

        if (dateIndex != -1) {
            // Вставляем перед датой
            lines.add(dateIndex, "$statusLabel $newStatus")
        } else {
            // Просто добавляем в конец
            lines.add("$statusLabel $newStatus")
        }
    }
}

/**
 * Удаляет строку статуса из записи
 */
fun removeStatusFromRecord(
    record: String, statusLabel: String    // "Статус:"
): String {
    val statusPattern = Regex("$statusLabel.*")
    val lines = record.lines().filterNot { statusPattern.matches(it.trim()) }
    return lines.joinToString("\n")
}