package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.model.incomes.IncomeRecord
import com.github.misham72.communalpayments.domain.repository.IncomeRepository

class GetIncomeRecordsUseCase(
    private val incomeRepository: IncomeRepository
) {
    suspend operator fun invoke(year: Int): List<IncomeRecord> {
        return incomeRepository.getRecordsByYear(year)
    }
}
