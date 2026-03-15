package com.github.misham72.communalpayments.presentation.screen.screens.electricity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R


@Composable
fun DisplayElectricityScreen(viewModel: ElectricityViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var tempNumber by remember { mutableStateOf(uiState.accountNumber) }
    // 🔸 ДОБАВИТЬ временную переменную для названия
    var tempName by remember { mutableStateOf(uiState.customServiceName) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // 🔸 ЗАМЕНИТЬ ВЕСЬ БЛОК ЗАГОЛОВКА И ИКОНКИ (вместо старого Row и отдельного Text)
        // Заголовок с иконкой (название сверху, иконка справа)
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (uiState.customServiceName.isNotBlank()) uiState.customServiceName
                else stringResource(R.string.service_display_name_electricity), fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.openAccountDialog() }) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.change_personal_account))
            }
        }

// Номер под названием (отдельная строка)
        if (uiState.accountNumber.isNotBlank()) {
            Text(
                text = "Л/С: ${uiState.accountNumber}", fontSize = 14.sp, color = Color.Red, modifier = Modifier.padding(top = 4.dp)
            )
        }
        // Поля ввода (как у тебя - с ресурсами)
        OutlinedTextField(
            value = uiState.currentReading, onValueChange = viewModel::onCurrentReadingChange, label = { Text(stringResource(R.string.current_reading_label_electricity)) }, modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.previousReading, onValueChange = viewModel::onPreviousReadingChange, label = { Text(stringResource(R.string.previous_reading_label_electricity)) }, modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.tariff, onValueChange = viewModel::onTariffChange, label = { Text(stringResource(R.string.tariff_label)) }, modifier = Modifier.fillMaxWidth()
        )

        // Кнопка
        Button(
            onClick = viewModel::onCalculateClick, modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.calculate_and_save))
        }
        // Ошибка
        uiState.errorMessage?.let {
            Text(it, color = Color.Red)
        }
        // Результат (как у тебя - с датой)
        uiState.result?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.result_electricity), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
                    )
                    Text(text = stringResource(R.string.consumption, result.consumption, stringResource(R.string.unit_kilowatt_hour)))
                    Text(
                        stringResource(R.string.currency_rub, result.payment), style = MaterialTheme.typography.headlineSmall, color = Color.Red
                    )
                }
            }
        }
    }
    // 🔸 ЗАМЕНИТЬ ДИАЛОГ на новый с двумя полями
    if (uiState.showAccountDialog) {
        AlertDialog(onDismissRequest = viewModel::closeAccountDialog, title = { Text("Редактирование") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tempName, onValueChange = { tempName = it }, label = { Text("Название услуги") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tempNumber, onValueChange = { tempNumber = it }, label = { Text("Лицевой счёт") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
            }
        }, confirmButton = {
            TextButton(
                onClick = {
                    viewModel.updateAccountData(tempNumber, tempName)
                    viewModel.closeAccountDialog()
                }) {
                Text(stringResource(R.string.save))
            }
        }, dismissButton = {
            TextButton(onClick = viewModel::closeAccountDialog) {
                Text(stringResource(R.string.cancel))
            }
        })
    }
}

