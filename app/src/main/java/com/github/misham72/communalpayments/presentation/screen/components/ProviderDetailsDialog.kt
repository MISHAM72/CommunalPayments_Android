package com.github.misham72.communalpayments.presentation.screen.components

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.ProviderDetails

@Composable
fun ProviderDetailsDialog(
    providerDetails: ProviderDetails,
    onDismiss: () -> Unit,
    onCopyText: (String) -> Unit,   // для копирования
    onOpenUrl: (String) -> Unit     // для открытия ссылки
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_details_to_copy)) },
        text = {
            Column {
                // Заголовок с названием услуги
                Text(
                    text = stringResource(R.string.service_label, providerDetails.customServiceName.ifBlank { stringResource(R.string.not_specified) }),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Лицевой счёт
                TextButton(
                    onClick = {
                        if (providerDetails.accountNumber.isNotBlank()) {
                            onCopyText(providerDetails.accountNumber)
                        } else {
                            Toast.makeText(context, R.string.personal_account_has_not_been_added, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (providerDetails.accountNumber.isNotBlank())
                            stringResource(R.string.personal_account, providerDetails.accountNumber)
                        else stringResource(R.string.personal_account_not_specified)
                    )
                }

                // Тариф
                TextButton(
                    onClick = {
                        if (providerDetails.tariff.isNotBlank()) {
                            onCopyText(providerDetails.tariff)
                        } else {
                            Toast.makeText(context, R.string.tariff_not_specified, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (providerDetails.tariff.isNotBlank())
                            stringResource(R.string.tariff_details, providerDetails.tariff)
                        else stringResource(R.string.tariff_not_specified)
                    )
                }

                // Название компании
                TextButton(
                    onClick = {
                        if (providerDetails.nameCompany.isNotBlank()) {
                            onCopyText(providerDetails.nameCompany)
                        } else {
                            Toast.makeText(context, R.string.name_company_not_specified, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (providerDetails.nameCompany.isNotBlank())
                            stringResource(R.string.name, providerDetails.nameCompany)
                        else stringResource(R.string.name_not_specified)
                    )
                }

                // ИНН
                TextButton(
                    onClick = {
                        if (providerDetails.inn.isNotBlank()) {
                            onCopyText(providerDetails.inn)
                        } else {
                            Toast.makeText(context, R.string.inn_not_added, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (providerDetails.inn.isNotBlank())
                            stringResource(R.string.personal_account_taxes, providerDetails.inn)
                        else stringResource(R.string.inn_not_specified)
                    )
                }

                // Расчётный счёт
                TextButton(
                    onClick = {
                        if (providerDetails.bankAccount.isNotBlank()) {
                            onCopyText(providerDetails.bankAccount)
                        } else {
                            Toast.makeText(context, R.string.bank_account_not_specified, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (providerDetails.bankAccount.isNotBlank())
                            stringResource(R.string.bank_account_label, providerDetails.bankAccount)
                        else stringResource(R.string.bank_account_label_not_specified)
                    )
                }

                // Сайт услуги (кликабельная ссылка)
                TextButton(
                    onClick = {
                        val url = providerDetails.websiteUrl.trim()
                        if (url.isNotBlank()) {
                            onOpenUrl(url)
                        } else {
                            Toast.makeText(context, R.string.website_not_specified, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (providerDetails.websiteUrl.isNotBlank())
                            stringResource(R.string.website_label_with_url, providerDetails.websiteUrl)
                        else stringResource(R.string.website_not_specified),
                        color = Color(0xFF0000EE), // яркий синий
                        // color = MaterialTheme.colorScheme.primary,
                        // Если хочешь подчёркивание – раскомментируй:
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
