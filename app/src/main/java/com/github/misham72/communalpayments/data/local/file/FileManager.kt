package com.github.misham72.communalpayments.data.local.file

import android.content.Context
import com.github.misham72.communalpayments.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException

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
}
