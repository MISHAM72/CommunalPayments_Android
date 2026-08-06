package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.model.IncomeSummary
import com.github.misham72.communalpayments.domain.repository.IncomeRepository

class GetYearlyIncomeUseCase(private val repository: IncomeRepository) {
    suspend operator fun invoke(year: Int): IncomeSummary {
        return repository.getYearlyIncome(year)
    }
}
