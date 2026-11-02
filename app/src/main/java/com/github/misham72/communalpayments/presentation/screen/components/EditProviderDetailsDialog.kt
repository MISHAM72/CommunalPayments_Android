package com.github.misham72.communalpayments.presentation.screen.components

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

@Composable
fun EditProviderDetailsDialog(
    details: ProviderDetails,
    customDate: String,
    onSave: (ProviderDetails, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Временные переменные для редактирования
    var tempDetails by remember { mutableStateOf(details) }
    var tempDate by remember { mutableStateOf(customDate) }

    val enterNameCompany = stringResource(R.string.enter_name_company)
    val enterInn = stringResource(R.string.enter_inn)
    val enterBankAccount = stringResource(R.string.enter_bank_account)
    val enterPersonalAccount = stringResource(R.string.enter_personal_account)


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_bank_details)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // 1. Название услуги
                OutlinedTextField(
                    value = tempDetails.customServiceName,
                    onValueChange = { tempDetails = tempDetails.copy(customServiceName = it) },
                    label = { Text(stringResource(R.string.service_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. Лицевой счёт
                OutlinedTextField(
                    value = tempDetails.accountNumber,
                    onValueChange = { tempDetails = tempDetails.copy(accountNumber = it) },
                    label = { Text(stringResource(R.string.personal_account_label)) },
                    isError = tempDetails.accountNumber.isBlank(),
                    supportingText = {
                        if (tempDetails.inn.isBlank()) {
                            Text(stringResource(R.string.enter_personal_account))
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. Тариф
                OutlinedTextField(
                    value = tempDetails.tariff,
                    onValueChange = { tempDetails = tempDetails.copy(tariff = it) },
                    label = { Text(stringResource(R.string.tariff)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 4. Название компании
                OutlinedTextField(
                    value = tempDetails.nameCompany,
                    onValueChange = { tempDetails = tempDetails.copy(nameCompany = it) },
                    label = { Text(stringResource(R.string.name_company)) },
                    isError = tempDetails.nameCompany.isBlank(),
                    supportingText = {
                        if (tempDetails.inn.isBlank()) {
                            Text(stringResource(R.string.enter_name_company))
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 5. ИНН
                OutlinedTextField(
                    value = tempDetails.inn,
                    onValueChange = { tempDetails = tempDetails.copy(inn = it) },
                    label = { Text(stringResource(R.string.inn_label)) },
                    isError = tempDetails.inn.isBlank(),
                    supportingText = {
                        if (tempDetails.inn.isBlank()) {
                            Text(stringResource(R.string.enter_inn))
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // 6. Расчётный счёт
                OutlinedTextField(
                    value = tempDetails.bankAccount,
                    onValueChange = { tempDetails = tempDetails.copy(bankAccount = it) },
                    label = { Text(stringResource(R.string.bank_account)) },
                    isError = tempDetails.bankAccount.isBlank(),
                    supportingText = {
                        if (tempDetails.inn.isBlank()) {
                            Text(stringResource(R.string.enter_bank_account))
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tempDetails.phone,
                    onValueChange = { tempDetails = tempDetails.copy(phone = it) },
                    label = { Text(stringResource(R.string.phone)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tempDetails.email,
                    onValueChange = { tempDetails = tempDetails.copy(email = it) },
                    label = { Text(stringResource(R.string.email)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = tempDetails.websiteUrl,  // ← берем из tempDetails
                    onValueChange = { tempDetails = tempDetails.copy(websiteUrl = it) }, // ← сохраняем в tempDetails
                    label = { Text(stringResource(R.string.website_label)) },
                    placeholder = { Text(stringResource(R.string.website_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // --- Выбор даты ---
                Button(
                    onClick = {
                        val now = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                val date = GregorianCalendar(year, month, day).time
                                val newDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
                                tempDate = newDate
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val missing = buildList {
                        if (tempDetails.nameCompany.isBlank()) add(enterNameCompany)
                        if (tempDetails.inn.isBlank()) add(enterInn)
                        if (tempDetails.bankAccount.isBlank()) add(enterBankAccount)
                        if (tempDetails.accountNumber.isBlank()) add(enterPersonalAccount)
                    }
                    if (missing.isNotEmpty()) {
                        Toast.makeText(context, missing.joinToString("\n"), Toast.LENGTH_LONG).show()
                        return@TextButton
                    }
                    onSave(tempDetails, tempDate)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
