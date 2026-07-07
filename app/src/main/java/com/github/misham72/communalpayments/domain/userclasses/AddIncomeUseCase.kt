package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.repository.IncomeRepository
import java.time.LocalDate

class AddIncomeUseCase(private val repository: IncomeRepository) {
    suspend operator fun invoke(year: Int, date: LocalDate, source: String, amount: Double) {
        repository.addIncome(year, date, source, amount)
    }
}
