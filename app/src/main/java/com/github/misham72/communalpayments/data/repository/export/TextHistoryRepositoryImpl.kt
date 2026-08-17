package com.github.misham72.communalpayments.data.repository.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.common.DataConstants
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.domain.repository.TextHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TextHistoryRepositoryImpl(
    private val fileManager: FileManager
) : TextHistoryRepository {
    // @Suppress("HardcodedStringLiteral")

    override suspend fun shareSingleHistory(context: Context, serviceKey: String): Unit = withContext(Dispatchers.IO) {
        val content = fileManager.readHistory(serviceKey)
        if (content.isBlank()) return@withContext

        val timeStamp = SimpleDateFormat(DataConstants.DATE_FORMAT_FILENAME, Locale.getDefault()).format(Date())
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
