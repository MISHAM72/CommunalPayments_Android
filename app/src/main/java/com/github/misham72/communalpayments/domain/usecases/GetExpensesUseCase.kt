package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.model.incomes.YearSummary
import com.github.misham72.communalpayments.domain.repository.AnalyticsRepository

class GetExpensesUseCase(private val repository: AnalyticsRepository) {
    suspend operator fun invoke(serviceKeys: List<String>, year: Int): Map<String, YearSummary> {
        return repository.getExpenses(serviceKeys, year)
    }
}
