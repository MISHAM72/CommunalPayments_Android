package com.github.misham72.communalpayments.domain.model

data class YearSummary(
    val total: Double,
    val monthly: Map<Int, Double>,   // ключ = номер месяца (1..12)
    val average: Double,
    val maxMonth: Int?,
    val minMonth: Int?
)