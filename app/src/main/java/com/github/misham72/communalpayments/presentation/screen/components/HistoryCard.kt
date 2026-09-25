package com.github.misham72.communalpayments.presentation.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.common.HistoryKeys
import com.github.misham72.communalpayments.domain.model.Attachment
import com.github.misham72.communalpayments.domain.model.HistoryRecord
import com.github.misham72.communalpayments.presentation.common.UiConstants
import java.time.LocalDate

@Suppress("HardcodedStringLiteral")
// Регулярки для скрытия служебных строк вложений (старый и новый формат)
private val attachmentLineRegex = Regex(
    """^(Вложение\d*:|ИмяФайла\d*:|MimeType\d*:).*$"""
)

@Composable
fun HistoryCard(
    record: HistoryRecord,
    serviceDisplayName: String,
    onAttachFile: () -> Unit,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenAttachment: (Attachment) -> Unit,
    onRemoveAttachment: (Attachment) -> Unit,
    modifier: Modifier = Modifier
) {
    var attachmentToDelete by remember { mutableStateOf<Attachment?>(null) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Blue.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // === Форматированный текст записи (без служебных строк) ===
            val formattedText = buildAnnotatedString {
                val lines = record.rawBlock.lines()
                val dateRegex = Regex(UiConstants.DATE_TIME_REGEX_PATTERN)
                val outputFormatter = java.time.format.DateTimeFormatter.ofPattern(
                    UiConstants.DATE_OUTPUT_PATTERN, UiConstants.DEFAULT_LOCALE
                )
                val toBePaidLabel = HistoryKeys.KEY_PAYMENT_PREFIX
                val cleanService = serviceDisplayName
                    .filter { it.isLetterOrDigit() }
                    .lowercase()

                lines.forEachIndexed { index, line ->
                    val trimmed = line.trim()

                    // Скрываем служебные строки вложений
                    if (attachmentLineRegex.matches(trimmed)) {
                        return@forEachIndexed
                    }

                    val isToBePaid = line.contains(toBePaidLabel)
                    val cleanTrimmed = trimmed
                        .filter { it.isLetterOrDigit() }
                        .lowercase()
                    val isServiceName = cleanTrimmed.isNotEmpty() &&
                        cleanService.isNotEmpty() &&
                        cleanTrimmed == cleanService
                    val dateMatch = dateRegex.find(line)
                    val isDate = dateMatch != null

                    when {
                        isToBePaid -> {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red,
                                    fontSize = 20.sp
                                )
                            ) { append(line) }
                        }

                        isDate -> {
                            val dateStr = dateMatch!!.groupValues[1]
                            val formattedDate = try {
                                LocalDate.parse(dateStr).format(outputFormatter)
                            } catch (_: Exception) {
                                dateStr
                            }
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 20.sp
                                )
                            ) { append(formattedDate) }
                        }

                        isServiceName -> {
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 20.sp
                                )
                            ) { append(serviceDisplayName) }
                        }

                        else -> append(line)
                    }

                    if (index < lines.size - 1) append("\n")
                }
            }

            Text(text = formattedText, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // === Список вложений ===
            if (record.hasAttachments) {
                record.attachments.forEach { attachment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📎 ${attachment.name}",
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = { onOpenAttachment(attachment) },
                            modifier = Modifier.width(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = stringResource(R.string.open),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { attachmentToDelete = attachment },
                            modifier = Modifier.width(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.delete_attachment),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // === Кнопки добавления: 📎 📷 🖼 ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onAttachFile) {
                    Icon(
                        imageVector = Icons.Filled.AttachFile,
                        contentDescription = stringResource(R.string.attach_file),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onOpenCamera) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = stringResource(R.string.open_camera),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onOpenGallery) {
                    Icon(
                        imageVector = Icons.Filled.PhotoLibrary,
                        contentDescription = stringResource(R.string.open_gallery),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    attachmentToDelete?.let { attachment ->
        AlertDialog(
            onDismissRequest = { attachmentToDelete = null },
            title = { Text(stringResource(R.string.delete_attachment)) },
            text = { Text(stringResource(R.string.file_label, attachment.name)) },
            confirmButton = {
                TextButton(onClick = {
                    onRemoveAttachment(attachment)   // ← удаляем ТОЛЬКО тут
                    attachmentToDelete = null         // ← закрываем диалог
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

