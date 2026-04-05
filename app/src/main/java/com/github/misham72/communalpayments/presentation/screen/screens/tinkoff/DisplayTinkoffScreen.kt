package com.github.misham72.communalpayments.presentation.screen.screens.tinkoff

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.ValidationError
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

@Composable
fun DisplayTinkoffScreen(viewModel: TinkoffViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var tempNumber by remember { mutableStateOf(uiState.accountNumber) }
    var tempName by remember { mutableStateOf(uiState.customServiceName) }
    var tempDate by remember { mutableStateOf(uiState.customDate) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
            .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(1.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = uiState.customServiceName.ifBlank { stringResource(R.string.service_display_name_tinkoff) }, fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.openAccountDialog() }) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.change_personal_account))
            }
        }
        if (uiState.customDate.isNotBlank()) {
            Text(
                text = stringResource(R.string.payment_date, uiState.customDate), fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp)
            )
        }
// Номер под названием (отдельная строка)
        if (uiState.accountNumber.isNotBlank()) {
            Text(
                text = stringResource(R.string.personal_account, uiState.accountNumber), fontSize = 14.sp, color = Color.Red, modifier = Modifier.padding(top = 4.dp)
            )
        }
        // Поле ввода - день платежа
        OutlinedTextField(
            value = uiState.paymentDay, onValueChange = viewModel::onPaymentDayChange, label = { Text(stringResource(R.string.day_of_payment_label)) },  // явный текст
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        // Поле ввода - период в месяцах
        OutlinedTextField(
            value = uiState.periodMonths, onValueChange = viewModel::onPeriodMonthsChange, label = { Text(stringResource(R.string.period_months_label)) },  // явный текст
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        // Поле ввода - тариф
        OutlinedTextField(
            value = uiState.priceTariff, onValueChange = viewModel::onPriceTariffChange, label = { Text(stringResource(R.string.tariff_label)) },  // явный текст
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )

        // Кнопка расчета
        Button(
            onClick = viewModel::onCalculateClick, modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.calculate_and_save))
        }

        when (uiState.error) {
            ValidationError.InvalidInput -> Text(
                stringResource(R.string.error_invalid_input),
                color = Color.Red
            )

            null -> { /* ничего */
            }
        }

        // Результат с ТВОИМИ ресурсами
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
                        text = stringResource(R.string.result_tinkoff), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
                    )

                    // ✅ ТВОИ оригинальные ресурсы!
                    Text(
                        text = stringResource(R.string.the_payment_was, result.previousPayment), fontWeight = FontWeight.Bold, color = Color(red = 0.02f, green = 0.4f, blue = 0.0f)
                    )

                    Text(
                        text = stringResource(R.string.passed, result.daysFromPayment), fontWeight = FontWeight.Bold, color = Color(red = 0.02f, green = 0.4f, blue = 0.0f)
                    )

                    Text(
                        text = stringResource(R.string.next_payment, result.nextPayment), fontWeight = FontWeight.Bold, color = Color.Red
                    )

                    Text(
                        text = stringResource(R.string.payment_in, result.daysUntilPayment), fontWeight = FontWeight.Bold, color = Color.Red
                    )

                    Text(
                        text = stringResource(R.string.tariff_card, result.priceTariff), style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
// 🔸 ЗАМЕНИТЬ ДИАЛОГ на новый с двумя полями
    if (uiState.showAccountDialog) {
        AlertDialog(onDismissRequest = viewModel::closeAccountDialog, title = { Text(stringResource(R.string.editing)) }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tempName, onValueChange = { tempName = it }, label = { Text(stringResource(R.string.service_name_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val now = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val date = GregorianCalendar(year, month, day).time
                                val newDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
                                tempDate = newDate  // обновляем временную переменную
                                // НЕ вызываем updateAccountData здесь!
                            },
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.select_start_date, tempDate))
                }
                OutlinedTextField(
                    value = tempNumber, onValueChange = { tempNumber = it }, label = { Text(stringResource(R.string.personal_account_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
            }
        }, confirmButton = {
            TextButton(
                onClick = {
                    viewModel.updateAccountData(tempNumber, tempName, tempDate)
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
