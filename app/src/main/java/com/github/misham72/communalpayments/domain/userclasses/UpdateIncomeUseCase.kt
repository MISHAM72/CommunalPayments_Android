package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.repository.IncomeRepository

class UpdateIncomeUseCase(private val repository: IncomeRepository) {
    suspend operator fun invoke(year: Int, content: String) {
        repository.updateIncome(year, content)
    }
}
