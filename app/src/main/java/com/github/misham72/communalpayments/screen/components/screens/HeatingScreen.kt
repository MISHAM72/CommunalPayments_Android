package com.github.misham72.communalpayments.screen.components.screens

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
import com.github.misham72.communalpayments.logic.ZONT


@Composable
fun DisplayHeatingScreen() {
    val context = LocalContext.current
    val newZONT = remember { ZONT(context) }
    val fileManager = remember { FileManager(context) }
    var newZONTData by remember { mutableStateOf<ZONT.ZONTData?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = {
                newZONTData = newZONT.collectZONTData()
                newZONT.saveZONTData(newZONTData ?: return@Button)

            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(context.getString(R.string.calculate_and_save))
        }

        if (newZONTData != null) {
            val data = newZONTData ?: return@Column
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.result_zont), fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.date, fileManager.getCurrentDateTime()))
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text(stringResource(R.string.previous_payment_with_value, data.previousPayment))
                    Text(stringResource(R.string.next_payment_with_value, data.nextPayment))
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text(stringResource(R.string.payment_made_with_value, data.daysFromPayment))
                    Text(
                        stringResource(R.string.next_payment_in_days, data.daysUntilPayment), fontWeight = FontWeight.Bold, color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // ← ДОБАВЬТЕ ЭТО
                    Text(stringResource(R.string.price_tariff, data.priceTariff.toDouble()))


                }
            }
        }
    }
}
