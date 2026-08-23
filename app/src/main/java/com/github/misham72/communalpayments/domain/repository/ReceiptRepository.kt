package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.Receipt
import java.io.File
import java.io.InputStream

interface ReceiptRepository {
    suspend fun saveReceipt(serviceKey: String, inputStream: InputStream, fileName: String): Receipt
    suspend fun getReceipts(serviceKey: String): List<Receipt>
    suspend fun deleteReceipt(id: String)
    suspend fun getReceiptFile(id: String): File?
}
