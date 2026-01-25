package com.github.misham72.communalpayments.presentation.screen.screens.taxes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.github.misham72.communalpayments.domain.userclasses.Taxes


@Composable
fun DisplayTaxesScreen() {
    val context = LocalContext.current
    val newTaxes = remember { Taxes(context) }
    val fileManager = remember { FileManager(context) } // ← ДОБАВИТЬ
    var newTaxesData by remember { mutableStateOf<Taxes.TaxesData?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = {
                newTaxesData = newTaxes.collectTaxesData()
                newTaxes.saveTaxesData(newTaxesData ?: return@Button)

            }

        ) {
            Text(stringResource(R.string.calculate_and_save))
        }

        if (newTaxesData != null) {
            val data = newTaxesData ?: return@Column
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.result_taxes), fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.date, fileManager.getCurrentDateTime()))
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text(
                        stringResource(R.string.previous_payment_with_value, data.previousPayment), fontWeight = FontWeight.Bold, color = Color(red = 0.02f, green = 0.4f, blue = 0.0f) // RGB значения 0-1
                    )
                    //оплата была
                    Text(
                        stringResource(R.string.payment_made_with_value, data.daysFromPayment), fontWeight = FontWeight.Bold, color = Color(red = 0.02f, green = 0.4f, blue = 0.0f)// оплата через
                    )   //прошло
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО

                    Text(
                        stringResource(R.string.next_payment_with_value, data.nextPayment), fontWeight = FontWeight.Bold, color = Color.Red
                    )   //дата оплаты
                    Text(
                        stringResource(R.string.days_until_payment, data.daysUntilPayment), fontWeight = FontWeight.Bold, color = Color.Red // оплата через
                    )   // оплата через
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text(stringResource(R.string.tariff, data.priceTariff))
                }
            }
        }
    }
}
