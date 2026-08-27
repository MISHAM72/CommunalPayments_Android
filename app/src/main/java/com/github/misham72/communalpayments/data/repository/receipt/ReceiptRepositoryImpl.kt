package com.github.misham72.communalpayments.data.repository.receipt

import android.util.Log
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.domain.model.Receipt
import com.github.misham72.communalpayments.domain.repository.ReceiptRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.UUID

class ReceiptRepositoryImpl(
    private val fileManager: FileManager,
    private val gson: Gson = Gson()
) : ReceiptRepository {

    private val indexFile: File
        get() = File(fileManager.getReceiptsDir(), "receipts_index.json")

    private fun loadIndex(): MutableMap<String, MutableList<Receipt>> {//Загрузка индекса (loadIndex()) – читает файл receipts_index.json, в котором хранится список всех квитанций для каждой услуги.
        return if (indexFile.exists()) {
            val json = indexFile.readText()
            val type = object : TypeToken<MutableMap<String, MutableList<Receipt>>>() {}.type
            gson.fromJson(json, type) ?: mutableMapOf()
        } else {
            mutableMapOf()
        }
    }

    private suspend fun saveIndex(index: Map<String, List<Receipt>>) {
        withContext(Dispatchers.IO) {
            val json = gson.toJson(index)
            indexFile.writeText(json)
        }
    }

    override suspend fun saveReceipt(serviceKey: String, inputStream: InputStream, fileName: String): Receipt {
        val path = fileManager.saveReceiptFile(inputStream, serviceKey, fileName)
        val receipt = Receipt(
            id = UUID.randomUUID().toString(),
            serviceKey = serviceKey,
            fileName = fileName,
            savedDate = System.currentTimeMillis(),
            filePath = path
        )
        val index = loadIndex()
        val list = index.getOrPut(serviceKey) { mutableListOf() }
        list.add(receipt)
        saveIndex(index)
        return receipt
    }

    override suspend fun getReceipts(serviceKey: String): List<Receipt> {
        return loadIndex()[serviceKey] ?: emptyList()
    }

    override suspend fun deleteReceipt(id: String) {
        val index = loadIndex()
        var found = false
        for ((_, list) in index) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val receipt = iterator.next()
                if (receipt.id == id) {
                    fileManager.deleteReceiptFile(receipt.filePath)
                    iterator.remove()
                    found = true
                    break
                }
            }
            if (found) break
        }
        if (found) saveIndex(index)
    }

    override suspend fun getReceiptFile(id: String): File? {
        val index = loadIndex()
        for ((_, list) in index) {
            for (receipt in list) {
                if (receipt.id == id) {
                    return fileManager.getReceiptFile(receipt.filePath)
                }
            }
        }
        return null
    }
}
