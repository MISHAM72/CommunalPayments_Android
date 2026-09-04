package com.github.misham72.communalpayments.presentation.screen.screens.receipts

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.Receipt
import java.io.File
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptsScreen(
    serviceKey: String,
    viewModel: ReceiptsViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

// Launcher для выбора PDF-файла
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileNameFromUri(context, it) ?: "receipt_${System.currentTimeMillis()}.pdf"
            val inputStream = context.contentResolver.openInputStream(it)
            inputStream?.let { stream ->
                viewModel.addReceipt(stream, fileName, serviceKey)
            }
        }
    }


    LaunchedEffect(serviceKey) {
        viewModel.loadReceipts(serviceKey)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.receipts_title, serviceKey)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { launcher.launch("application/pdf") }
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_receipt))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is ReceiptsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ReceiptsUiState.Error -> {
                    Text(
                        text = stringResource(R.string.error_prefix, (uiState as ReceiptsUiState.Error).message),
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is ReceiptsUiState.Success -> {
                    val receipts = (uiState as ReceiptsUiState.Success).receipts
                    if (receipts.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_receipts),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn {
                            items(receipts) { receipt ->
                                val openPdfTitle = stringResource(R.string.open_pdf)
                                ReceiptItem(
                                    receipt = receipt,
                                    onOpen = {
                                        val file = File(receipt.filePath)
                                        if (file.exists()) {
                                            val uri = FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.fileprovider",
                                                file
                                            )
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(uri, "application/pdf")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(Intent.createChooser(intent, openPdfTitle))
                                        }
                                    },
                                    onDelete = {
                                        viewModel.deleteReceipt(receipt.id, serviceKey)
                                    }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp)) // высота FAB + отступ
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptItem(
    receipt: Receipt,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val dateFormatString = stringResource(R.string.date_time_format)
    val dateFormat = remember(locale, dateFormatString) {
        SimpleDateFormat(dateFormatString, locale)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = receipt.fileName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = dateFormat.format(receipt.savedDate),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Row {
                IconButton(onClick = onOpen) {
                    Icon(Icons.Outlined.Visibility, contentDescription = stringResource(R.string.open), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        }
    }
}

// Вспомогательная функция для получения имени файла из Uri
private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && it.moveToFirst()) {
            it.getString(nameIndex)
        } else null
    }
}
