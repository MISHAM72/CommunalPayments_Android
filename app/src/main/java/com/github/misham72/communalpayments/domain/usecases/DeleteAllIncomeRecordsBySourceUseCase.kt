package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.repository.IncomeRepository

class DeleteAllIncomeRecordsBySourceUseCase(
    private val incomeRepository: IncomeRepository
) {
    suspend operator fun invoke(year: Int, source: String) {
        incomeRepository.deleteAllRecordsBySource(year, source)
    }
}
