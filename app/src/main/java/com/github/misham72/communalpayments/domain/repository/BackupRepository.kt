package com.github.misham72.communalpayments.domain.repository


import java.io.InputStream
import java.io.OutputStream

interface BackupRepository {
    suspend fun exportData(outputStream: OutputStream): Boolean
    suspend fun importData(inputStream: InputStream): Boolean
}
