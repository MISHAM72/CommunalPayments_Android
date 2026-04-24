package com.github.misham72.communalpayments.domain.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HistoryExporter {
    private const val DATE_FORMAT_FILENAME = "yyyyMMdd_HHmmss"
    suspend fun shareSingleHistory(context: Context, serviceKey: String): Unit = withContext(Dispatchers.IO) {
        val fileManager = FileManager(context.applicationContext)
        val content = fileManager.readHistory(serviceKey)
        if (content.isBlank()) return@withContext

        val timeStamp = SimpleDateFormat(DATE_FORMAT_FILENAME, Locale.getDefault()).format(Date())
        val fileName = "payment_history_${serviceKey}_$timeStamp.txt"
        val file = File(context.cacheDir, fileName)
        file.writeText(content)

        withContext(Dispatchers.Main) {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.export_history)))
        }
    }
}