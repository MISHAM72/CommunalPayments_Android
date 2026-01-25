package com.github.misham72.communalpayments.presentation.screen.screens.history

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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager


@Composable
//Тело функции — это шаблон/рецепт, который говорит:
//"Когда меня вызывают, я беру переданные onBack и initialService, и по этому рецепту строю целый рабочий экран с историей расчетов."

fun SimpleHistoryScreen(
    onBack: () -> Unit, initialService: String  // ← ПРИНИМАЕМ УСЛУГУ
) {
    val context = LocalContext.current   //Мы получаем контекст (context) (доступ к Android-системе) и создаем несколько состояний (state) для управления UI.
    // 2. Создаём состояния UI (переменные, которые меняют внешний вид)
    val loadingText = stringResource(R.string.loading)
    var fileContent by remember { mutableStateOf(loadingText) }   //fileContent: хранит содержимое файла истории для выбранной услуги.

    var isEditing by remember { mutableStateOf(false) }   //isEditing: флаг, указывающий, находится ли пользователь в режиме редактирования.

    var selectedService by remember { mutableStateOf(initialService) }  // selectedService: выбранная услуга (изначально берется из параметра initialService).

    val fileManager = remember { FileManager(context) }   //Создаем экземпляр FileManager для работы с файлами.

    fun loadHistoryData() {   //Определяем функцию loadHistoryData(), которая загружает историю для выбранной услуги и обновляет состояние fileContent.

        fileContent = fileManager.loadFromFile(selectedService)
    }



    LaunchedEffect(selectedService) {   //Используем LaunchedEffect для загрузки данных при изменении selectedService.

        fileContent = loadingText
        loadHistoryData()
    }
    //Строим UI с помощью Compose:
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), content = {
            Button(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {  //Кнопка "Назад", при нажатии на которую вызывается переданный в параметре onBack callback.
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
                    stringResource(R.string.service_key_electricity) to stringResource(R.string.service_display_name_electricity), // ← ключ : отображаемое имя
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

                ).forEach { (service, name) ->  //Горизонтальный список FilterChip для выбора услуги. При нажатии на чип меняется selectedService, что вызывает перезагрузку истории для новой услуги.

                    FilterChip(selected = selectedService == service, onClick = { selectedService = service }, label = { Text(name) })// ← показывает "Электричество", "Газ" и т.д
                }
            }
            //В зависимости от isEditing показываем либо поле для редактирования текста (OutlinedTextField) с кнопками "Сохранить" и "Отмена", либо Card с отображением истории (в виде HTML) и кнопкой "Редактировать историю".
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
                Column {
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Простая замена для HTML
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
    )
}