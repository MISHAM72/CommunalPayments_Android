package com.github.misham72.communalpayments.presentation.screen.screens.analytics.income

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.Attachment
import com.github.misham72.communalpayments.domain.model.incomes.IncomeRecord
import com.github.misham72.communalpayments.presentation.common.UiConstants
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.IncomeViewModel
import java.io.File
import kotlin.collections.isNotEmpty


@Composable
fun SourceRecordsDialog(
    source: String,
    records: List<IncomeRecord>,
    viewModel: IncomeViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var editIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<IncomeRecord?>(null) }
    var recordForAttach by remember { mutableStateOf<IncomeRecord?>(null) }
    var attachmentToDelete by remember { mutableStateOf<Pair<IncomeRecord, Attachment>?>(null) }
    // 📎 — любой файл
    val attachLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val record = recordForAttach
        if (uri != null && record != null) {
            val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

            /** «Создай переменную fileName. Попробуй получить имя файла из uri.
             *  Если не получилось — сгенерируй имя сам,
             * используя текущее время».*/
            val fileName = getFileNameFromUri(context, uri) ?: UiConstants.FILE_NAME_TEMPLATE.format(System.currentTimeMillis())
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (bytes != null && bytes.isNotEmpty()) {
                viewModel.attachAttachment(record, bytes, fileName, mimeType)
            }
        }
        recordForAttach = null
    }

    // 📷 — камера
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: android.graphics.Bitmap? ->
        val record = recordForAttach
        if (bitmap != null && record != null) {
            val tempFile = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
            tempFile.outputStream().use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            }
            val bytes = tempFile.readBytes()
            tempFile.delete()
            if (bytes.isNotEmpty()) {
                viewModel.attachAttachment(
                    record, bytes,
                    "photo_${System.currentTimeMillis()}.jpg",
                    "image/jpeg"
                )
            }
        }
        recordForAttach = null
    }

    // 🖼 — галерея
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val record = recordForAttach
        if (uri != null && record != null) {
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val fileName = getFileNameFromUri(context, uri) ?: UiConstants.IMAGE_NAME_TEMPLATE.format(System.currentTimeMillis())
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            if (bytes != null && bytes.isNotEmpty()) {
                viewModel.attachAttachment(record, bytes, fileName, mimeType)
            }
        }
        recordForAttach = null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.income_dialog_title, source)) },
        text = {
            if (records.isEmpty()) {
                Text(stringResource(R.string.no_records))
            } else {
                LazyColumn {
                    items(records.size) { index ->
                        val record = records[index]
                        val isEditing = editIndex == index

                        if (isEditing) {
                            var editSource by remember { mutableStateOf(record.source) }
                            var editAmount by remember { mutableStateOf(record.amount.toString()) }

                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                OutlinedTextField(
                                    value = editSource,
                                    onValueChange = { editSource = it },
                                    label = { Text(stringResource(R.string.source)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = editAmount,
                                    onValueChange = { editAmount = it },
                                    label = { Text(stringResource(R.string.amount)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TextButton(onClick = {
                                        val newAmount = editAmount.toDoubleOrNull()
                                        if (newAmount != null && newAmount > 0 && editSource.isNotBlank()) {
                                            // ✅ сохраняем вложения при редактировании
                                            val newRecord = record.copy(
                                                amount = newAmount,
                                                source = editSource
                                            )
                                            viewModel.updateRecord(record, newRecord)
                                            editIndex = null
                                        }
                                    }) {
                                        Text(stringResource(R.string.save))
                                    }
                                    TextButton(onClick = { editIndex = null }) {
                                        Text(stringResource(R.string.cancel))
                                    }
                                }
                            }
                        } else {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = stringResource(R.string.money_format).format(record.amount),
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = record.date.toString(),
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            recordForAttach = record
                                            attachLauncher.launch("*/*")
                                        }) {
                                            Icon(
                                                Icons.Default.AttachFile,
                                                contentDescription = stringResource(R.string.attach_file),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = {
                                            recordForAttach = record
                                            cameraLauncher.launch(null)
                                        }) {
                                            Icon(
                                                Icons.Default.PhotoCamera,
                                                contentDescription = stringResource(R.string.open_camera),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = {
                                            recordForAttach = record
                                            galleryLauncher.launch(UiConstants.MIME_TYPE_IMAGE)
                                        }) {
                                            Icon(
                                                Icons.Default.PhotoLibrary,
                                                contentDescription = stringResource(R.string.open_gallery),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = { editIndex = index }) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = stringResource(R.string.editing),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = { showDeleteConfirm = record }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                // Вложения
                                record.attachments.forEach { attachment ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 8.dp, top = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "📎 ${attachment.name}",
                                            fontSize = 12.sp,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    openIncomeAttachment(context, attachment, viewModel)
                                                },
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = {
                                                attachmentToDelete = record to attachment
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = stringResource(R.string.delete),
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
//✅ Это диалог подтверждения удаления записи дохода
    val deleteRecord = showDeleteConfirm
    if (deleteRecord != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text(stringResource(R.string.delete_record)) },
            text = { Text(stringResource(R.string.delete_confirm_amount).format(deleteRecord.amount)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRecord(deleteRecord)
                    showDeleteConfirm = null
                    onDismiss()
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    // Диалог подтверждения удаления вложения
    val pairToDelete = attachmentToDelete
    if (pairToDelete != null) {
        val (record, attachment) = pairToDelete
        AlertDialog(
            onDismissRequest = { attachmentToDelete = null },
            title = { Text(stringResource(R.string.delete_attachment)) },
            text = { Text(stringResource(R.string.delete_attachment_confirm, attachment.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeAttachment(record, attachment)
                    attachmentToDelete = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { attachmentToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

