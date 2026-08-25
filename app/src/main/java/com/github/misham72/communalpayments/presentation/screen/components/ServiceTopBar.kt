package com.github.misham72.communalpayments.presentation.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    onReceiptsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPdfExport: () -> Unit = {}

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
            // 🆕 Кнопка квитанций (папка)
            IconButton(
                onClick = {
                    // звук не обязателен, можно добавить позже
                    onReceiptsClick()
                }
            ) {
                Icon(
                    Icons.Outlined.Folder,
                    contentDescription = stringResource(R.string.receipts) // или stringResource, если добавишь в strings.xml
                )
            }


            Box {
                IconButton(// 1. Кнопка с иконкой "Поделиться"
                    onClick = {
                        clockCuCuSound?.start()// звук
                        showMenu = true// открываем меню
                    }
                ) {
                    Icon(// иконка шеринга
                        Icons.Default.Share,
                        contentDescription = stringResource(R.string.export_history)
                    )
                }
                DropdownMenu(// 2. Выпадающее меню (показывается, если showMenu == true)
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(// Пункт меню "Экспорт TXT"
                        text = { Text(stringResource(R.string.export_txt)) },
                        onClick = {
                            showMenu = false
                            onShareClick()// ← вызывает переданный колбэк для TXT
                        }
                    )
                    DropdownMenuItem(// Пункт меню "Экспорт PDF"
                        text = { Text(stringResource(R.string.export_pdf)) },
                        onClick = {
                            showMenu = false
                            onPdfExport()// ← ВОТ ЗДЕСЬ ВЫЗЫВАЕТСЯ PDF-ЭКСПОРТ
                        }
                    )
                }
            }
        }
    }
}
