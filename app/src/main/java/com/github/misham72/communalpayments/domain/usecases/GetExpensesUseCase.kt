package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.model.incomes.YearSummary
import com.github.misham72.communalpayments.domain.repository.ExpensesRepository

class GetExpensesUseCase(private val repository: ExpensesRepository) {
    suspend operator fun invoke(
        serviceKeys: List<String>, year: Int,
        month: Int? = null
    ): Map<String, YearSummary> {
        return if (month == null) {
            repository.getYearlyExpenses(serviceKeys, year)
        } else {
            repository.getMonthlyExpenses(serviceKeys, year, month)
        }
    }
}
