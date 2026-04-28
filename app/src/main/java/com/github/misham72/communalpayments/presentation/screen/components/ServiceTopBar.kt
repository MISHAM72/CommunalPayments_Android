package com.github.misham72.communalpayments.presentation.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.presentation.utils.rememberChangeListSoundPlayer
import com.github.misham72.communalpayments.presentation.utils.rememberClockCuCuSoundPlayer

@Composable
fun ServiceTopBar(
    title: String,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPdfExport: () -> Unit = {}   // новый параметр с пустой реализацией по умолчанию

) {
    val clockCuCuSound = rememberClockCuCuSoundPlayer()
    val changeListSound = rememberChangeListSoundPlayer()
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(33.dp)
            .padding(horizontal = 2.dp),
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
                onClick = {
                    changeListSound?.start()
                    onEditClick()
                }
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.change_personal_account)
                )
            }

            // Кнопка, открывающая меню
            Box {
                IconButton(
                    onClick = {
                        clockCuCuSound?.start()
                        showMenu = true
                    }
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = stringResource(R.string.export_history)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Поделиться TXT") },
                        onClick = {
                            showMenu = false
                            onShareClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Экспорт PDF") },
                        onClick = {
                            showMenu = false
                            onPdfExport()
                        }
                    )
                }
            }
        }
    }
}