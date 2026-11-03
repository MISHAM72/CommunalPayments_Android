package com.github.misham72.communalpayments.presentation.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.misham72.communalpayments.R

@Composable
fun ServicePickerDialog(
    serviceItems: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_service_receipt)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                serviceItems.forEach { (label, key) ->
                    TextButton(
                        onClick = { onSelect(key) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(label)
                    }
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
