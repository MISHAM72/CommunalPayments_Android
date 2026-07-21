package com.github.misham72.communalpayments.presentation.screen.screens.heating

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
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
import com.github.misham72.communalpayments.presentation.screen.components.EditProviderDetailsDialog
import com.github.misham72.communalpayments.presentation.screen.components.ServiceTopBar
import com.github.misham72.communalpayments.presentation.utils.BankPaymentHelper
import com.github.misham72.communalpayments.presentation.utils.HistoryExporter
import com.github.misham72.communalpayments.presentation.utils.rememberBankButtonSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberCoinSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberCopyButtonSoundPlayer
import kotlinx.coroutines.launch

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun DisplayZONTScreen(viewModel: ZONTViewModel) {
    val showBankDialog = remember { mutableStateOf(false) }
    val showProviderDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coinSound = rememberCoinSoundPlayer()
    val copySound = rememberCopyButtonSoundPlayer()
    val bankSound = rememberBankButtonSoundPlayer()
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        ServiceTopBar(
            onPdfExport = { viewModel.onPdfExport(context) },
            title = uiState.providerDetails.customServiceName.ifBlank { stringResource(R.string.service_display_name_zont) },
            onEditClick = { viewModel.openAccountDialog() },
            onShareClick = {
                scope.launch {
                    HistoryExporter.shareSingleHistory(context, ZONTViewModel.SERVICE_KEY)
                }
            },
            modifier = Modifier.height(28.dp)
        )
        if (uiState.customDate.isNotBlank()) {
            Text(
                text = stringResource(R.string.payment_date, uiState.customDate), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 1.dp)
            )
        }
// Номер под названием (отдельная строка)
        if (uiState.providerDetails.accountNumber.isNotBlank()) {
            Text(
                text = stringResource(R.string.number_phone, uiState.providerDetails.accountNumber), fontSize = 14.sp, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 1.dp)
            )
        }
        // Поле ввода - день платежа
        OutlinedTextField(
            value = uiState.paymentDay,
            onValueChange = viewModel::onPaymentDayChange,
            label = { Text(stringResource(R.string.day_of_payment_label)) },  // явный текст
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp, max = 56.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        )

        // Поле ввода - период в месяцах
        OutlinedTextField(
            value = uiState.periodMonths,
            onValueChange = viewModel::onPeriodMonthsChange,
            label = { Text(stringResource(R.string.period_months_label)) },  // явный текст
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp, max = 56.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        )

        // Поле ввода - тариф
        OutlinedTextField(
            value = uiState.providerDetails.tariff,
            onValueChange = viewModel::onPriceTariffChange,
            label = { Text(stringResource(R.string.tariff_label)) },  // явный текст
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp, max = 56.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        )
        Spacer(modifier = Modifier.height(3.dp))
        Button(
            onClick = {
                coinSound?.start()
                viewModel.onCalculateClick()
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.calculate_and_save))
        }

        when (uiState.error) {
            ValidationError.InvalidInput -> Text(
                stringResource(R.string.error_invalid_input),
                color = Color.Red
            )

            ValidationError.SavingError -> Text(
                stringResource(R.string.error_saving),
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
                        text = stringResource(R.string.result_zont), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
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
            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка ПОД Card
            Button(
                onClick = {
                    copySound?.start()
                    clipboardManager.setText(AnnotatedString(result.priceTariff.toString()))
                    Toast.makeText(context, context.getString(R.string.amount_copied, result.priceTariff), Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.copy_amount))
            }
            Button(
                onClick = {
                    bankSound?.start()
                    showBankDialog.value = true
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.select_bank_to_pay))
            }
            // Кнопка, открывающая диалог с выбором реквизитов
            Button(
                onClick = {
                    copySound?.start()
                    showProviderDialog.value = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.payment_details))
            }
        }
        Spacer(modifier = Modifier.height(10.dp)) // небольшой отступ для красоты
    }
    if (showBankDialog.value) {
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
        /**В AlertDialog мы проверяем список банков через PackageManager,
         * фильтруем только установленные и показываем их кнопками.*/
        AlertDialog(onDismissRequest = { showBankDialog.value = false }, title = { Text(stringResource(R.string.select_bank)) }, text = {
            Column {
                if (installedBanks.isEmpty()) {
                    Text(stringResource(R.string.there_are_no_installed_banking_applications))
                } else {
                    installedBanks.forEach { bank ->
                        TextButton(
                            onClick = {
                                showBankDialog.value = false
                                BankPaymentHelper.openBankApp(context, bank)
                            }) {
                            Text(bank.name)
                        }
                    }
                }
            }
        }, confirmButton = {
            TextButton(onClick = { showBankDialog.value = false }) {
                Text(stringResource(R.string.cancel))
            }
        })
    }
    // Диалог выбора: ИНН или Л/С
    if (showProviderDialog.value) {
        AlertDialog(
            onDismissRequest = { showProviderDialog.value = false },
            title = { Text(stringResource(R.string.select_details_to_copy)) },
            text = {
                Column {
                    // 1. Название услуги (только текст)
                    Text(
                        text = stringResource(R.string.service_label, uiState.providerDetails.customServiceName.ifBlank { stringResource(R.string.not_specified) }),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    // 5. Лицевой счёт
                    TextButton(
                        onClick = {
                            if (uiState.providerDetails.accountNumber.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(uiState.providerDetails.accountNumber))
                                Toast.makeText(context, R.string.your_personal_account_has_been_copied, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, R.string.personal_account_has_not_been_added, Toast.LENGTH_LONG).show()
                            }
                            showProviderDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (uiState.providerDetails.accountNumber.isNotBlank())
                                stringResource(R.string.personal_account, uiState.providerDetails.accountNumber)
                            else stringResource(R.string.personal_account_not_specified)
                        )
                    }

                    // 2. Тариф (копируется)
                    TextButton(
                        onClick = {
                            if (uiState.providerDetails.tariff.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(uiState.providerDetails.tariff))
                                Toast.makeText(context, R.string.tariff_copied, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, R.string.tariff_not_specified, Toast.LENGTH_LONG).show()
                            }
                            showProviderDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (uiState.providerDetails.tariff.isNotBlank())
                                stringResource(R.string.tariff_details, uiState.providerDetails.tariff)
                            else stringResource(R.string.tariff_not_specified)
                        )
                    }

                    // 3. Название компании
                    TextButton(
                        onClick = {
                            if (uiState.providerDetails.nameCompany.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(uiState.providerDetails.nameCompany))
                                Toast.makeText(context, R.string.name_company_copied, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, R.string.name_company_not_specified, Toast.LENGTH_LONG).show()
                            }
                            showProviderDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (uiState.providerDetails.nameCompany.isNotBlank())
                                stringResource(R.string.name, uiState.providerDetails.nameCompany)
                            else stringResource(R.string.name_not_specified)
                        )
                    }

                    // 4. ИНН
                    TextButton(
                        onClick = {
                            if (uiState.providerDetails.inn.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(uiState.providerDetails.inn))
                                Toast.makeText(context, R.string.inn_copied, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, R.string.inn_not_added, Toast.LENGTH_LONG).show()
                            }
                            showProviderDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (uiState.providerDetails.inn.isNotBlank())
                                stringResource(R.string.personal_account_taxes, uiState.providerDetails.inn)
                            else stringResource(R.string.inn_not_specified)
                        )
                    }

                    // 6. Расчётный счёт
                    TextButton(
                        onClick = {
                            if (uiState.providerDetails.bankAccount.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(uiState.providerDetails.bankAccount))
                                Toast.makeText(context, R.string.bank_account_copied, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.bank_account_not_specified, Toast.LENGTH_LONG
                                ).show()
                            }
                            showProviderDialog.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (uiState.providerDetails.bankAccount.isNotBlank())
                                stringResource(R.string.bank_account_label, uiState.providerDetails.bankAccount)
                            else stringResource(R.string.bank_account_label_not_specified)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProviderDialog.value = false }) {
                    Text(stringResource(R.string.bank_account_label_not_specified))
                }
            }
        )
    }
    if (uiState.showAccountDialog) {
        EditProviderDetailsDialog(
            details = uiState.providerDetails,
            customDate = uiState.customDate,
            onSave = { updatedDetails, updatedDate ->
                viewModel.saveProviderDetails(updatedDetails)
                viewModel.updateCustomDate(updatedDate)
                viewModel.closeAccountDialog()
            },
            onDismiss = { viewModel.closeAccountDialog() }
        )
    }
}
