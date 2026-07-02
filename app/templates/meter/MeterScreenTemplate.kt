package com.github.misham72.communalpayments.presentation.screen.screens.electricity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.ElectricityRepository
import com.github.misham72.communalpayments.domain.userclasses.Electricity

@Composable
fun ElectricityScreen(
    viewModel: ElectricityViewModel = viewModel(
        factory = ElectricityViewModelFactory(
            Electricity(), ElectricityRepository(LocalContext.current, fileManager = FileManager(LocalContext.current))
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                    Text(stringResource(R.string.consumption, result.consumption, R.string.unit_kilowatt_hour))
                    Text(
                        text = stringResource(R.string.currency_rub, result.payment), style = MaterialTheme.typography.headlineSmall, color = Color.Red
                    )
                }
            }
        }
    }
}
