package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.Attachment
import com.github.misham72.communalpayments.domain.model.incomes.IncomeRecord
import com.github.misham72.communalpayments.domain.model.incomes.IncomeSummary
import java.io.File
import java.time.LocalDate

interface IncomeRepository {
    suspend fun getYearlyIncome(year: Int): IncomeSummary
    suspend fun getMonthlyIncome(year: Int, month: Int): IncomeSummary
    suspend fun addIncome(year: Int, date: LocalDate, source: String, amount: Double)
    suspend fun updateIncome(year: Int, content: String)

    // Новые методы для управления записями
    suspend fun updateRecord(year: Int, oldRecord: IncomeRecord, newRecord: IncomeRecord)
    suspend fun deleteRecord(year: Int, record: IncomeRecord)
    suspend fun deleteAllRecordsBySource(year: Int, source: String)
    suspend fun getRecordsByYear(year: Int): List<IncomeRecord>
    suspend fun attachAttachment(year: Int, record: IncomeRecord, bytes: ByteArray, fileName: String, mimeType: String)
    suspend fun removeAttachment(year: Int, record: IncomeRecord, attachment: Attachment)
    fun getAttachment(path: String): File?
}

