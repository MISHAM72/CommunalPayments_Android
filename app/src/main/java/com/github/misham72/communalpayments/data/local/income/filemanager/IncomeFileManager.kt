package com.github.misham72.communalpayments.data.local.income.filemanager

import com.github.misham72.communalpayments.data.common.DataConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class IncomeFileManager(private val filesDir: File) {

    private val directory: File
        get() = File(filesDir, DataConstants.INCOME_HISTORY_DIR).also { it.mkdirs() }

    private fun fileForYear(year: Int): File = File(directory, "income_$year.txt")

    suspend fun readIncome(year: Int): String = withContext(Dispatchers.IO) {
        val file = fileForYear(year)
        if (file.exists()) file.readText() else ""
    }

    fun appendIncome(year: Int, recordText: String) {
        val file = fileForYear(year)
        val existing = if (file.exists()) file.readText() else ""
        val newText = if (existing.isNotEmpty()) {
            "$recordText\n***\n$existing"
        } else {
            recordText
        }
        file.writeText(newText)
    }

    suspend fun saveIncome(year: Int, content: String) {
        withContext(Dispatchers.IO) {
            val file = fileForYear(year)
            file.writeText(content)
        }
    }

    private fun getIncomeAttachmentsDir(year: Int): File {
        val dir = File(File(filesDir, DataConstants.INCOME_ATTACHMENTS_DIR), year.toString())
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    suspend fun saveAttachment(year: Int, bytes: ByteArray, fileName: String): String {
        return withContext(Dispatchers.IO) {
            val dir = getIncomeAttachmentsDir(year)
            val uniqueName = "${System.currentTimeMillis()}_$fileName"
            val file = File(dir, uniqueName)
            file.writeBytes(bytes)
            file.absolutePath
        }
    }

    fun deleteAttachment(path: String): Boolean {
        return File(path).delete()
    }

    fun getAttachment(path: String): File? {
        val file = File(path)
        return if (file.exists()) file else null
    }
}
