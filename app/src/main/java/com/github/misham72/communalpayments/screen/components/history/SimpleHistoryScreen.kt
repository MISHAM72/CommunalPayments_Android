package com.github.misham72.communalpayments.screen.components.history

import android.widget.TextView

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager


@Composable
fun SimpleHistoryScreen(
    onBack: () -> Unit, initialService: String  // ← ПРИНИМАЕМ УСЛУГУ
) {
    val context = LocalContext.current
    var fileContent by remember { mutableStateOf("") }

    var isEditing by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf(initialService) }  // ← ИСПОЛЬЗУЕМ ПЕРЕДАННУЮ УСЛУГУ

    val fileManager = remember { FileManager(context) }

    fun loadHistoryData() {
        fileContent = fileManager.loadFromFile(selectedService)
    }

    LaunchedEffect(selectedService) {
        fileContent = context.getString(R.string.loading)
        loadHistoryData()
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Text(stringResource(R.string.back))
        }

        Text(stringResource(R.string.calculation_history), fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Выбор услуги
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .horizontalScroll(rememberScrollState()),  // ← ДОБАВЬ ПРОКРУТКУ в истории расчетов это
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                //Ключ: "electricity" (для файлов и логики) Название: "Свет" (для отображения)
                // FilterChip в истории сравнивает ключи.
                context.getString(R.string.service_key_electricity) to context.getString(R.string.service_display_name_electricity), // ← ключ : отображаемое имя
                context.getString(R.string.service_key_gas) to context.getString(R.string.service_display_name_gas),
                context.getString(R.string.service_key_water) to context.getString(R.string.service_display_name_water),
                context.getString(R.string.service_key_zont) to context.getString(R.string.service_display_name_zont),
                context.getString(R.string.service_key_internet) to context.getString(R.string.service_display_name_internet),
                context.getString(R.string.service_key_mts) to context.getString(R.string.service_display_name_mts),
                context.getString(R.string.service_key_tinkoff) to context.getString(R.string.service_display_name_tinkoff),
                context.getString(R.string.service_key_garbage) to context.getString(R.string.service_display_name_garbage),
                context.getString(R.string.service_key_taxes) to context.getString(R.string.service_display_name_taxes),
                context.getString(R.string.service_key_troyka) to context.getString(R.string.service_display_name_troyka),
                context.getString(R.string.service_key_osago) to context.getString(R.string.service_display_name_osago)

            ).forEach { (service, name) ->
                FilterChip(selected = selectedService == service, onClick = { selectedService = service }, label = { Text(name) })// ← показывает "Электричество", "Газ" и т.д
            }
        }

        if (isEditing) {
            OutlinedTextField(
                value = fileContent, onValueChange = { fileContent = it }, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), maxLines = Int.MAX_VALUE
            )

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
        } else {
            Card(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)  // ← БЕЛЫЙ ФОН
            ) {
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            textSize = 16f
                            setTextIsSelectable(true)
                        }
                    }, update = { view ->
                        view.text = HtmlCompat.fromHtml(
                            fileContent, HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            Button(
                onClick = { isEditing = true }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.Edit_history))
            }
        }
    }
}
