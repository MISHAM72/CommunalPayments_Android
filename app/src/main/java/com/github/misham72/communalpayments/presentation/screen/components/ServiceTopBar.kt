package com.github.misham72.communalpayments.presentation.screen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.presentation.utils.rememberChangeListSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberClockCuCuSoundPlayer

@Composable
fun ServiceTopBar(
    title: String,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clockCuCuSound = rememberClockCuCuSoundPlayer()
    val changeListSound = rememberChangeListSoundPlayer()
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Row {
            IconButton(
                onClick =
                    {
                        changeListSound?.start()
                        onEditClick()
                    }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.change_personal_account)
                )
            }
            IconButton(
                onClick =
                    {
                        clockCuCuSound?.start()
                        onShareClick()
                    }) {

                Icon(
                    Icons.Default.Share,
                    contentDescription = stringResource(R.string.export_history)
                )
            }
        }
    }
}
