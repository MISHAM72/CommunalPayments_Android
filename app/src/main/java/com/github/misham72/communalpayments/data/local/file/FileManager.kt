package com.github.misham72.communalpayments.data.local.file

import android.content.Context
import com.github.misham72.communalpayments.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream

class FileManager(private val context: Context) {
    //Проверяет, существует ли папка history.
    suspend fun readHistory(serviceKey: String): String = withContext(Dispatchers.IO) {
        val directory = File(context.filesDir, context.getString(R.string.history))
        if (!directory.exists()) {
            return@withContext context.getString(R.string.empty_history_calculation)
        }//Проверяет, существует ли файл <serviceKey>.txt.
        val file = File(directory, "$serviceKey.txt")
        if (!file.exists()) {
            return@withContext context.getString(R.string.empty_history_calculation)
        }//Если файл есть — читает его содержимое как текст и возвращает.
        return@withContext file.readText()
    }


    suspend fun appendRecord(serviceKey: String, recordText: String) {
        withContext(Dispatchers.IO) {
            val directory = File(context.filesDir, context.getString(R.string.history))
            directory.mkdirs()
            val file = File(directory, "$serviceKey.txt")
            val existingText = if (file.exists()) file.readText() else ""
            val newText = if (existingText.isNotEmpty()) {
                "$recordText\n\n$existingText"
            } else {
                recordText
            }
            file.writeText(newText)
        }
    }

    // Сохранение нового файла
    fun saveToFile(content: String, fileName: String): Boolean {
        return try {
            val directory = File(context.filesDir, context.getString(R.string.history))
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, fileName)
            FileWriter(file).use { writer ->
                writer.write(content)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    // ---------- Методы для квитанций ----------
    fun getReceiptsDir(): File {
        val dir = File(context.filesDir, context.getString(R.string.receipts))
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun getServiceReceiptsDir(serviceKey: String): File {
        val dir = File(getReceiptsDir(), serviceKey)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    suspend fun saveReceiptFile(inputStream: InputStream, serviceKey: String, fileName: String): String {
        return withContext(Dispatchers.IO) {
            val dir = getServiceReceiptsDir(serviceKey)
            val uniqueFileName = "${System.currentTimeMillis()}_$fileName"
            val file = File(dir, uniqueFileName)
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            file.absolutePath
        }
    }

    fun deleteReceiptFile(path: String): Boolean {
        return File(path).delete()
    }

    fun getReceiptFile(path: String): File? {
        val file = File(path)
        return if (file.exists()) file else null
    }
}
