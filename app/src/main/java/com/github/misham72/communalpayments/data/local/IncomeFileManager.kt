package com.github.misham72.communalpayments.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class IncomeFileManager(private val context: Context) {

    private val directory: File
        @Suppress("HardcodedStringLiteral")
        get() = File(context.filesDir, "income_history").also { it.mkdirs() }

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
}
