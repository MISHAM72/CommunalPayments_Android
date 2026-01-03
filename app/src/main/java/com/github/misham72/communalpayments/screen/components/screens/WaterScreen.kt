package com.github.misham72.communalpayments.screen.components.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.logic.Water
import kotlin.text.format


@Composable
fun DisplayWaterScreen() {
    val context = LocalContext.current  // ← вызов контента
    val newWater = remember { Water(context) } // Передаем context
    val fileManager = remember { FileManager(context) } // ← ДОБАВИТЬ ЭТУ СТРОКУ

    var currentReading by remember { mutableStateOf("") }
    var previousReading by remember { mutableStateOf("") }
    var tariff by remember { mutableStateOf("") }
    var consumption by remember { mutableStateOf("") }
    var payment by remember { mutableStateOf("") }


    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        OutlinedTextField(
            value = currentReading, onValueChange = { currentReading = it }, label = { Text(stringResource(R.string.current_reading_label_water)) }, modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = previousReading, onValueChange = { previousReading = it }, label = { Text(stringResource(R.string.previous_reading_label_water)) }, modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tariff, onValueChange = { tariff = it }, label = { Text(stringResource(R.string.tariff_label)) }, modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val current = currentReading.toDoubleOrNull() ?: 0.0
                val previous = previousReading.toDoubleOrNull() ?: 0.0
                val tariffValue = tariff.toDoubleOrNull() ?: 0.0

                val result = newWater.collectWaterData(current, previous, tariffValue)  // 1. collectWaterData СОБИРАЕТ объект из введённых чисел val result теперь содержит ВСЕ данные для отображения
                consumption = context.getString(R.string.consumption).format(result.consumption, result.unit)  // Из waterData
                payment = context.getString(R.string.currency_rub).format(result.payment)           // Из waterData
                newWater.saveWaterData(result)                          // Внутри себя готовит readyHeader, readyService...
                // Сохранение теперь происходит автоматически в calculate()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(context.getString(R.string.calculate_and_save))
        }

    }

    if (consumption.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.result_water), fontWeight = FontWeight.Bold)
                // Text(context.getString(R.string.consumption_label), fontWeight = FontWeight.Bold)

                // Text(context.getString(R.string.payment_label), fontWeight = FontWeight.Bold, color = Color.Red)
                // ✅ ПРАВИЛЬНО: используем ваши реальные переменные
                // Вариант А: Просто выводим готовые строки
                Text(consumption)
                Text(payment, fontWeight = FontWeight.Bold, color = Color.Red) // "102.50 руб."

                Text(stringResource(R.string.date, fileManager.getCurrentDateTime())) // ← ИСПОЛЬЗУЕМ fileManager
            }
        }
    }
}
