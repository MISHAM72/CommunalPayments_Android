package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.incomes.IncomeRecord
import com.github.misham72.communalpayments.domain.model.incomes.IncomeSummary
import java.time.LocalDate

interface IncomeRepository {
    suspend fun getYearlyIncome(year: Int): IncomeSummary
    suspend fun addIncome(year: Int, date: LocalDate, source: String, amount: Double)
    suspend fun updateIncome(year: Int, content: String)

    // Новые методы для управления записями
    suspend fun updateRecord(year: Int, oldRecord: IncomeRecord, newRecord: IncomeRecord)
    suspend fun deleteRecord(year: Int, record: IncomeRecord)
    suspend fun deleteAllRecordsBySource(year: Int, source: String)
    suspend fun getRecordsByYear(year: Int): List<IncomeRecord>
}

