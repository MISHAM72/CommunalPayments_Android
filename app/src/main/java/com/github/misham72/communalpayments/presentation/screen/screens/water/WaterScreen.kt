package com.github.misham72.communalpayments.presentation.screen.screens.water

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
import com.github.misham72.communalpayments.domain.userclasses.Water


@Composable
fun DisplayWaterScreen() {
    val context = LocalContext.current  // ← вызов контента
    val newWater = remember { Water(context) } // Передаем context
    val fileManager = remember { FileManager(context) } // ← ДОБАВИТЬ ЭТУ СТРОКУ

    var currentReading by remember { mutableStateOf("") }
    var previousReading by remember { mutableStateOf("") }
    var tariff by remember { mutableStateOf("") }
    var waterResult by remember { mutableStateOf<Water.WaterData?>(null) }

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
                waterResult = result
                newWater.saveWaterData(result)                          // Внутри себя готовит readyHeader, readyService...

            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.calculate_and_save))
        }

    }

    waterResult?.let { result ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.result_water), fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.consumption, result.consumption, result.unit))
                Text(stringResource(R.string.currency_rub, result.payment), fontWeight = FontWeight.Bold, color = Color.Red)
                Text(stringResource(R.string.date, fileManager.getCurrentDateTime())) // ← ИСПОЛЬЗУЕМ fileManager
            }
        }
    }
}
