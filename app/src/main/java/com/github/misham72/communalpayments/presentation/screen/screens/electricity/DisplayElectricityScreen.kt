package com.github.misham72.communalpayments.presentation.screen.screens.electricity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.utils.HistoryExporter
import com.github.misham72.communalpayments.presentation.screen.components.ServiceTopBar
import com.github.misham72.communalpayments.presentation.utils.BankPaymentHelper
import com.github.misham72.communalpayments.presentation.utils.rememberBankButtonSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberCoinSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberCopyButtonSoundPlayer
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun DisplayElectricityScreen(viewModel: ElectricityViewModel) {
    var showBankDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current   // <-- добавить
    val clipboardManager = LocalClipboardManager.current
    val coinSound = rememberCoinSoundPlayer()
    val copySound = rememberCopyButtonSoundPlayer()
    val bankSound = rememberBankButtonSoundPlayer()
    val uiState by viewModel.uiState.collectAsState()
    var tempNumber by remember { mutableStateOf(uiState.accountNumber) }
    var tempName by remember { mutableStateOf(uiState.customServiceName) }
    var tempDate by remember { mutableStateOf(uiState.customDate) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        ServiceTopBar(title = uiState.customServiceName.ifBlank { stringResource(R.string.service_display_name_electricity) }, onEditClick = { viewModel.openAccountDialog() }, onShareClick = {
            scope.launch {
                HistoryExporter.shareSingleHistory(context, viewModel.getServiceKey())
            }
        })
        if (uiState.customDate.isNotBlank()) {
            Text(
                text = stringResource(R.string.payment_date, uiState.customDate), fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp)
            )
        }
        if (uiState.accountNumber.isNotBlank()) {
            Text(
                text = stringResource(R.string.personal_account, uiState.accountNumber), fontSize = 14.sp, color = Color.Red, modifier = Modifier.padding(top = 4.dp)
            )
        }
        OutlinedTextField(
            value = uiState.currentReading, onValueChange = viewModel::onCurrentReadingChange, label = { Text(stringResource(R.string.current_reading_label_electricity)) }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.previousReading, onValueChange = viewModel::onPreviousReadingChange, label = { Text(stringResource(R.string.previous_reading_label_electricity)) }, modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.tariff, onValueChange = viewModel::onTariffChange, label = { Text(stringResource(R.string.tariff_label)) }, modifier = Modifier.fillMaxWidth()
        )
        //
        Button(
            onClick = {
                coinSound?.start()
                viewModel::onCalculateClick.invoke()
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.calculate_and_save))
        }
        when (uiState.error) {
            ValidationError.InvalidInput -> Text(
                stringResource(R.string.error_invalid_input), color = Color.Red
            )

            ValidationError.SavingError -> Text(
                stringResource(R.string.error_saving), color = Color.Red
            )

            null -> { /* ничего */
            }
        }
        //Карточка с результатом и кнопкой копировать
        uiState.result?.let { result ->
            // Только Card (без кнопки)
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
                    Text(
                        text = stringResource(
                            R.string.consumption, result.consumption, stringResource(R.string.unit_kilowatt_hour)
                        )
                    )
                    Text(
                        text = stringResource(R.string.currency_rub, result.payment), style = MaterialTheme.typography.headlineSmall, color = Color.Red
                    )
                }
            }
            // Отступ
            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка ПОД Card
            Button(
                onClick = {
                    copySound?.start()
                    clipboardManager.setText(AnnotatedString(result.payment.toString()))
                    Toast.makeText(context, context.getString(R.string.amount_copied, result.payment), Toast.LENGTH_SHORT).show()
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.copy_amount))
            }
            Button(
                onClick = {
                    bankSound?.start()
                    showBankDialog = true
                }, modifier = Modifier.fillMaxWidth()
            ) {
                /** Кнопка "Выбрать банк для оплаты" в экране расчета вызывает
                 * showBankDialog = true, что отображает AlertDialog.*/
                Text(stringResource(R.string.select_bank_to_pay))
            }
        }
    }
    if (showBankDialog) {
        // Проверяем, какие банки из нашего списка установлены на телефоне
        val installedBanks = remember {
            BankPaymentHelper.supportedBanks.filter { bank ->
                try {
                    context.packageManager.getPackageInfo(bank.packageName, 0)
                    true
                } catch (_: Exception) {
                    false
                }
            }
        }

        AlertDialog(onDismissRequest = { showBankDialog = false }, title = { Text(stringResource(R.string.select_bank)) }, text = {
            Column {
                if (installedBanks.isEmpty()) {
                    Text(stringResource(R.string.there_are_no_installed_banking_applications))
                } else {
                    installedBanks.forEach { bank ->
                        TextButton(
                            onClick = {
                                showBankDialog = false
                                BankPaymentHelper.openBankApp(context, bank)
                            }) {
                            Text(bank.name)
                        }
                    }
                }
            }
        }, confirmButton = {
            TextButton(onClick = { showBankDialog = false }) {
                Text(stringResource(R.string.cancel))
            }
        })
    }
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
                            context, { _, year, month, day ->
                                val date = GregorianCalendar(year, month, day).time
                                val newDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
                                tempDate = newDate  // обновляем временную переменную
                                // НЕ вызываем updateAccountData здесь!
                            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.select_start_date, tempDate))
                }
                // Поле для номера счёта
                OutlinedTextField(
                    value = tempNumber, onValueChange = { tempNumber = it }, label = { Text(stringResource(R.string.personal_account_label)) }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
            }
        }, confirmButton = {
            TextButton(
                onClick = {
                    viewModel.updateAccountData(tempNumber, tempName, tempDate) // сохраняем всё
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