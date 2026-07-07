package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.repository.BackupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupRepositoryImpl(
    private val context: Context
) : BackupRepository {

    // Папка с историей платежей
    private val historyDir: File
        get() = File(context.filesDir, context.getString(R.string.history))

    // Папка с доходами
    private val incomeDir: File
        @Suppress("HardcodedStringLiteral")
        get() = File(context.filesDir, "income_history")

    @Suppress("HardcodedStringLiteral")
    override suspend fun exportData(outputStream: OutputStream): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                ZipOutputStream(outputStream).use { zipOut ->
                    // 1. Добавляем файлы из папки История
                    val historyFiles = historyDir.listFiles()?.filter { it.isFile && it.extension == "txt" } ?: emptyList()
                    for (file in historyFiles) {
                        @Suppress("HardcodedStringLiteral")
                        val entry = ZipEntry("history/${file.name}")
                        zipOut.putNextEntry(entry)
                        file.inputStream().use { input -> input.copyTo(zipOut) }
                        zipOut.closeEntry()
                    }

                    // 2. Добавляем файлы из папки income_history
                    if (incomeDir.exists()) {
                        val incomeFiles = incomeDir.listFiles()?.filter { it.isFile && it.extension == "txt" } ?: emptyList()
                        for (file in incomeFiles) {
                            @Suppress("HardcodedStringLiteral")
                            val entry = ZipEntry("income_history/${file.name}")
                            zipOut.putNextEntry(entry)
                            file.inputStream().use { input -> input.copyTo(zipOut) }
                            zipOut.closeEntry()
                        }
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    @Suppress("HardcodedStringLiteral")
    override suspend fun importData(inputStream: InputStream): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Очищаем папку История перед распаковкой (удаляем все .txt)
                historyDir.listFiles()?.filter { it.isFile && it.extension == "txt" }?.forEach { it.delete() }

                // Очищаем папку income_history (удаляем все .txt)
                if (incomeDir.exists()) {
                    incomeDir.listFiles()?.filter { it.isFile && it.extension == "txt" }?.forEach { it.delete() }
                } else {
                    incomeDir.mkdirs() // создаём, если не существует
                }

                ZipInputStream(inputStream).use { zipIn ->
                    var entry = zipIn.nextEntry
                    while (entry != null) {
                        val fullPath = entry.name
                        when {
                            fullPath.startsWith("history/") -> {
                                val fileName = fullPath.removePrefix("history/")
                                val outputFile = File(historyDir, fileName)
                                outputFile.outputStream().use { output ->
                                    zipIn.copyTo(output)
                                }
                            }

                            fullPath.startsWith("income_history/") -> {
                                val fileName = fullPath.removePrefix("income_history/")
                                val outputFile = File(incomeDir, fileName)
                                outputFile.outputStream().use { output ->
                                    zipIn.copyTo(output)
                                }
                            }

                            else -> {
                                // Если файл лежит в корне архива (старый формат) – игнорируем
                            }
                        }
                        zipIn.closeEntry()
                        entry = zipIn.nextEntry
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
