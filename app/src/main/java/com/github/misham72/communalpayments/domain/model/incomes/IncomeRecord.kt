package com.github.misham72.communalpayments.domain.model.incomes

import java.time.LocalDate

data class IncomeRecord(
    val date: LocalDate,   // теперь полная дата
    val amount: Double,
    val source: String
)
