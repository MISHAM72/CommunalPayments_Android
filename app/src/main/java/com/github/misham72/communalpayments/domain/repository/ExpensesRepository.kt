package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.incomes.YearSummary

interface ExpensesRepository {
    suspend fun getYearlyExpense(serviceKey: String, year: Int): YearSummary
    suspend fun getYearlyExpenses(serviceKeys: List<String>, year: Int): Map<String, YearSummary>
    suspend fun getMonthlyExpenses(serviceKeys: List<String>, year: Int, month: Int): Map<String, YearSummary>
}
