package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.incomes.IncomeSummary
import java.time.LocalDate

interface IncomeRepository {
    suspend fun getYearlyIncome(year: Int): IncomeSummary
    suspend fun addIncome(year: Int, date: LocalDate, source: String, amount: Double)
    suspend fun updateIncome(year: Int, content: String)
}

