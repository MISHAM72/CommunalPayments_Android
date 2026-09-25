package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.model.Attachment
import com.github.misham72.communalpayments.domain.model.incomes.IncomeRecord
import com.github.misham72.communalpayments.domain.model.incomes.IncomeSummary
import com.github.misham72.communalpayments.domain.repository.IncomeRepository
import java.io.File
import java.time.LocalDate

class IncomeUseCase(
    private val incomeRepository: IncomeRepository
) {
    suspend fun getYearlyIncome(year: Int): IncomeSummary {
        return incomeRepository.getYearlyIncome(year)
    }

    suspend fun getMonthlyIncome(year: Int, month: Int): IncomeSummary {
        return incomeRepository.getMonthlyIncome(year, month)
    }

    suspend fun getIncomes(year: Int): List<IncomeRecord> {
        return incomeRepository.getRecordsByYear(year)
    }

    suspend fun addIncome(year: Int, date: LocalDate, source: String, amount: Double) {
        incomeRepository.addIncome(year, date, source, amount)
    }

    suspend fun updateIncome(year: Int, oldRecord: IncomeRecord, newRecord: IncomeRecord) {
        incomeRepository.updateRecord(year, oldRecord, newRecord)
    }

    suspend fun deleteIncome(year: Int, record: IncomeRecord) {
        incomeRepository.deleteRecord(year, record)
    }

    suspend fun deleteAllBySource(year: Int, source: String) {
        incomeRepository.deleteAllRecordsBySource(year, source)
    }

    suspend fun attachAttachment(
        year: Int,
        record: IncomeRecord,
        bytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        incomeRepository.attachAttachment(year, record, bytes, fileName, mimeType)
    }

    suspend fun removeAttachment(year: Int, record: IncomeRecord, attachment: Attachment) {
        incomeRepository.removeAttachment(year, record, attachment)
    }

    fun getAttachment(path: String): File? {
        return incomeRepository.getAttachment(path)
    }
}
