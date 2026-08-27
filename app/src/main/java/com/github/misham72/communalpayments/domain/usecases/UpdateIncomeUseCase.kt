package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.repository.IncomeRepository

class UpdateIncomeUseCase(
    private val incomeRepository: IncomeRepository
) {
    suspend fun updateIncome(year: Int, content: String) {
        incomeRepository.updateIncome(year, content)
    }
}
