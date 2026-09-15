package com.github.misham72.communalpayments.domain.model.incomes

import com.github.misham72.communalpayments.domain.model.Attachment
import java.time.LocalDate

data class IncomeRecord(
    val date: LocalDate,
    val amount: Double,
    val source: String,
    val attachments: List<Attachment> = emptyList()
)
