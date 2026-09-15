package com.github.misham72.communalpayments.data.repository.backup

import com.github.misham72.communalpayments.data.common.DataConstants
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
    private val filesDir: File,
    private val historyDirName: String,
    private val incomeDirName: String
) : BackupRepository {

    private val historyDir: File
        get() = File(filesDir, historyDirName)

    private val incomeDir: File
        get() = File(filesDir, incomeDirName)

    // Новые папки для бэкапа
    private val historyAttachmentsDir: File
        get() = File(filesDir, DataConstants.HISTORY_ATTACHMENTS_DIR)

    private val incomeAttachmentsDir: File
        get() = File(filesDir, DataConstants.INCOME_ATTACHMENTS_DIR)

    private val receiptsDir: File
        get() = File(filesDir, DataConstants.RECEIPTS_DIR)

    @Suppress("HardcodedStringLiteral")
    override suspend fun exportData(outputStream: OutputStream): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                ZipOutputStream(outputStream).use { zipOut ->
                    // 1. История (.txt)
                    addFilesFlat(zipOut, historyDir, "history/")

                    // 2. Доходы (.txt)
                    addFilesFlat(zipOut, incomeDir, "income_history/")

                    // 3. Вложения к истории (рекурсивно, все файлы)
                    addDirRecursively(zipOut, historyAttachmentsDir, "history_attachments/")

                    // 4. Вложения к доходам (рекурсивно)
                    addDirRecursively(zipOut, incomeAttachmentsDir, "income_attachments/")

                    // 5. Квитанции — архив по услугам (рекурсивно)
                    addDirRecursively(zipOut, receiptsDir, "receipts/")
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
                // Очищаем ВСЕ папки перед распаковкой
                clearDir(historyDir)
                clearDir(incomeDir)
                clearDirAll(historyAttachmentsDir)   // ← полностью
                clearDirAll(incomeAttachmentsDir)    // ← полностью
                clearDirAll(receiptsDir)             // ← полностью

                ZipInputStream(inputStream).use { zipIn ->
                    var entry = zipIn.nextEntry
                    while (entry != null) {
                        val fullPath = entry.name

                        when {
                            fullPath.startsWith("history/") -> {
                                val fileName = fullPath.removePrefix("history/")
                                writeEntry(zipIn, File(historyDir, fileName))
                            }

                            fullPath.startsWith("income_history/") -> {
                                val fileName = fullPath.removePrefix("income_history/")
                                writeEntry(zipIn, File(incomeDir, fileName))
                            }

                            fullPath.startsWith("history_attachments/") -> {
                                val relative = fullPath.removePrefix("history_attachments/")
                                writeEntry(zipIn, File(historyAttachmentsDir, relative))
                            }

                            fullPath.startsWith("income_attachments/") -> {
                                val relative = fullPath.removePrefix("income_attachments/")
                                writeEntry(zipIn, File(incomeAttachmentsDir, relative))
                            }

                            fullPath.startsWith("receipts/") -> {
                                val relative = fullPath.removePrefix("receipts/")
                                writeEntry(zipIn, File(receiptsDir, relative))
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

    // ---------- Вспомогательные методы ----------

    /**
     * Добавляет в ZIP плоский список файлов из папки (только указанного расширения).
     * Используется для .txt файлов истории и доходов.
     */
    private fun addFilesFlat(
        zipOut: ZipOutputStream,
        dir: File,
        prefixInZip: String
    ) {
        if (!dir.exists()) return
        val files = dir.listFiles()
            ?.filter { it.isFile && it.extension == DataConstants.TXT_EXTENSION }
            ?: emptyList()

        for (file in files) {
            val entry = ZipEntry("$prefixInZip${file.name}")
            zipOut.putNextEntry(entry)
            file.inputStream().use { it.copyTo(zipOut) }
            zipOut.closeEntry()
        }
    }

    /**
     * Рекурсивно добавляет папку в ZIP со всеми подпапками и файлами.
     */
    private fun addDirRecursively(
        zipOut: ZipOutputStream,
        dir: File,
        prefixInZip: String
    ) {
        if (!dir.exists()) return

        dir.walkTopDown().forEach { file ->
            if (file.isFile) {
                // Относительный путь внутри папки (например, "hostel/1234_photo.jpg")
                val relativePath = file.relativeTo(dir).path.replace("\\", "/")
                val entry = ZipEntry("$prefixInZip$relativePath")
                zipOut.putNextEntry(entry)
                file.inputStream().use { it.copyTo(zipOut) }
                zipOut.closeEntry()
            }
        }
    }

    /**
     * Записывает очередной entry в файл, создавая все подпапки.
     */
    private fun writeEntry(zipIn: ZipInputStream, outputFile: File) {
        outputFile.parentFile?.mkdirs()
        outputFile.outputStream().use { output ->
            zipIn.copyTo(output)
        }
    }

    /**
     * Очищает папку от файлов с указанным расширением (папку не удаляет).
     */
    private fun clearDir(dir: File) {
        if (!dir.exists()) {
            dir.mkdirs()
            return
        }
        dir.listFiles()
            ?.filter { it.isFile && it.extension == DataConstants.TXT_EXTENSION }
            ?.forEach { it.delete() }
    }

    /**
     * Полностью очищает папку (все файлы и подпапки). Папку не удаляет.
     */
    private fun clearDirAll(dir: File) {
        if (!dir.exists()) {
            dir.mkdirs()
            return
        }
        dir.deleteRecursively()
        dir.mkdirs()
    }
}
