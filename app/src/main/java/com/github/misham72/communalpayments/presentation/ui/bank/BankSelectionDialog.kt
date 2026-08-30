package com.github.misham72.communalpayments.presentation.ui.bank

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.di.AppContainer
import com.github.misham72.communalpayments.domain.model.Bank
import com.github.misham72.communalpayments.presentation.mapper.BankLogoMapper
import com.github.misham72.communalpayments.presentation.utils.BankPaymentHelper

@Composable
fun BankSelectionDialog(
    onDismiss: () -> Unit,
    onBankSelected: (Bank) -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { AppContainer.bankRepository }
    val banks = remember { repository.getSupportedBanks() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_bank)) },
        text = {
            LazyColumn {
                items(banks) { bank ->
                    val logoResId = BankLogoMapper.getLogoResId(bank.logoResId)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                BankPaymentHelper.openBankApp(context, bank)
                                onBankSelected(bank)
                                onDismiss()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        if (logoResId != 0) {
                            Image(
                                painter = painterResource(id = logoResId),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        } else {
                            Text("🏦", fontSize = 32.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = bank.name, style = MaterialTheme.typography.titleMedium)
                    }
                    HorizontalDivider()
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}
