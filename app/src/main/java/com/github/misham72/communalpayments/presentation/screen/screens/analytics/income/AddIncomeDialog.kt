package com.github.misham72.communalpayments.presentation.screen.screens.analytics.income

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.incomes.IncomeCategory
import com.github.misham72.communalpayments.presentation.utils.nameRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun AddIncomeDialog(
    onDismiss: () -> Unit, onAdd: (source: String, amount: Double) -> Unit
) {
    val categories = IncomeCategory.entries
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var amountText by remember { mutableStateOf("") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.add_income)) }, text = {
        Column {
            OutlinedTextField(
                value = amountText, onValueChange = { amountText = it }, label = { Text(stringResource(R.string.amount)) }, singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(stringResource(R.string.source), style = MaterialTheme.typography.labelMedium)
            categories.forEach { category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCategory = category }
                        .padding(vertical = 4.dp)) {
                    RadioButton(
                        selected = selectedCategory == category, onClick = { selectedCategory = category })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(category.nameRes()))
                }
            }
        }
    }, confirmButton = {
        TextButton(onClick = {
            val amount = amountText.toDoubleOrNull()
            if (amount != null && amount > 0) {
                onAdd(selectedCategory.name, amount)  // ← передаём английский ключ
            }
        }) {
            Text(stringResource(R.string.add))
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(stringResource(R.string.cancel))
        }
    })
}
