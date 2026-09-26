package com.github.misham72.communalpayments.presentation.screen.screens.analytics.income

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.github.misham72.communalpayments.domain.model.Attachment
import com.github.misham72.communalpayments.presentation.screen.screens.analytics.IncomeViewModel

fun openIncomeAttachment(
    context: android.content.Context,
    attachment: Attachment,
    viewModel: IncomeViewModel
) {
    val file = viewModel.getAttachmentFile(attachment.path) ?: return
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, attachment.mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, attachment.name))
}

fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    return cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && it.moveToFirst()) it.getString(nameIndex) else null
    }
}
