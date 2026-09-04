package com.github.misham72.communalpayments.presentation.screen.screens.gas

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.di.AppContainer
import com.github.misham72.communalpayments.di.ReceiptsViewModelFactory
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.presentation.screen.components.EditProviderDetailsDialog
import com.github.misham72.communalpayments.presentation.screen.components.ProviderDetailsDialog
import com.github.misham72.communalpayments.presentation.screen.components.ServiceTopBar
import com.github.misham72.communalpayments.presentation.screen.screens.receipts.ReceiptsScreen
import com.github.misham72.communalpayments.presentation.screen.screens.receipts.ReceiptsViewModel
import com.github.misham72.communalpayments.presentation.ui.bank.BankSelectionDialog
import com.github.misham72.communalpayments.presentation.utils.normalizeUrl
import com.github.misham72.communalpayments.presentation.utils.rememberBankButtonSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberCoinSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberCopyButtonSoundPlayer

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun GasScreen(viewModel: GasViewModel, appContainer: AppContainer) {
    val showBankDialog = remember { mutableStateOf(false) }
    val showProviderDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coinSound = rememberCoinSoundPlayer()
    val copySound = rememberCopyButtonSoundPlayer()
    val bankSound = rememberBankButtonSoundPlayer()
    val uiState by viewModel.uiState.collectAsState()

    // ----- Квитанции -----
    val receiptsViewModelFactory = ReceiptsViewModelFactory(appContainer.getReceiptsUseCase, appContainer.deleteReceiptUseCase, appContainer.saveReceiptUseCase)
    val receiptsViewModel: ReceiptsViewModel = viewModel(factory = receiptsViewModelFactory)
    var showReceipts by remember { mutableStateOf(false) }
    if (showReceipts) {
        ReceiptsScreen(
            serviceKey = GasViewModel.SERVICE_KEY,
            viewModel = receiptsViewModel,
            onBack = { showReceipts = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(1.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            ServiceTopBar(
                title = uiState.providerDetails.customServiceName.ifBlank { stringResource(R.string.service_display_name_gas) },
                onEditClick = { viewModel.openAccountDialog() },
                onTxtExport = { viewModel.onShareClick(context) },
                modifier = Modifier.height(28.dp),
                onPdfExport = { viewModel.onPdfExport(context) },
                onReceiptsClick = { showReceipts = true }
            )
            if (uiState.customDate.isNotBlank()) {
                Text(
                    text = stringResource(R.string.payment_date, uiState.customDate),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
            if (uiState.providerDetails.accountNumber.isNotBlank()) {
                Text(
                    text = stringResource(R.string.personal_account, uiState.providerDetails.accountNumber),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
            OutlinedTextField(
                value = uiState.currentReading,
                onValueChange = viewModel::onCurrentReadingChange,
                label = { Text(stringResource(R.string.current_reading_txt_water_and_gas)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp), // сужаем
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    lineHeight = 20.sp
                )
            )

            OutlinedTextField(
                value = uiState.previousReading,
                onValueChange = viewModel::onPreviousReadingChange,
                label = { Text(stringResource(R.string.previous_reading_txt_water_and_gas)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp), // сужаем
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    lineHeight = 20.sp
                )
            )

            OutlinedTextField(
                value = uiState.providerDetails.tariff,
                onValueChange = viewModel::onTariffChange,
                label = { Text(stringResource(R.string.tariff_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp), // сужаем
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    lineHeight = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(3.dp))
            Button(
                onClick = {
                    coinSound?.start()
                    viewModel.onCalculateClick()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.calculate_and_save))
            }
            @Suppress("VariableDeclarationInWhen")
            val error = uiState.error
            when (error) {
                ValidationError.InvalidInput -> Text(
                    stringResource(R.string.error_invalid_input),
                    color = Color.Red
                )

                ValidationError.SavingError -> Text(
                    stringResource(R.string.error_saving),
                    color = Color.Red
                )

                is ValidationError.DomainError -> Text(
                    text = error.message,
                    color = Color.Red
                )

                null -> { /* ничего */
                }
            }
            // Карточка с результатом (всегда видна)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                val result = uiState.lastResult
                if (result != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.result_gas),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(
                                R.string.consumption,
                                result.consumption.value,
                                stringResource(R.string.unit_cubic_meter)
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.currency_rub, result.payment.amount),
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.Red
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(result.payment.amount.toString()))
                                    Toast.makeText(
                                        context, context.getString(R.string.amount_copied, result.payment.amount),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.ContentCopy,
                                    contentDescription = stringResource(R.string.copy_amount)
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_saved_result), // нужно добавить в strings.xml
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    bankSound?.start()
                    showBankDialog.value = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.select_bank_to_pay))
            }
            Button(
                onClick = {
                    copySound?.start()
                    showProviderDialog.value = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.payment_details))
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (showBankDialog.value) {
            BankSelectionDialog(
                onDismiss = { showBankDialog.value = false },
                appContainer = appContainer
            )
        }
        // Диалог выбора: ИНН или Л/С
        if (showProviderDialog.value) {
            ProviderDetailsDialog(
                providerDetails = uiState.providerDetails,
                onDismiss = { showProviderDialog.value = false },
                onCopyText = { text ->
                    clipboardManager.setText(AnnotatedString(text))
                    Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
                }, onOpenUrl = { url ->
                    val finalUrl = url.normalizeUrl()
                    val intent = Intent(Intent.ACTION_VIEW, finalUrl.toUri())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        Toast.makeText(context, R.string.no_browser, Toast.LENGTH_SHORT).show()
                    }
                })
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
}

