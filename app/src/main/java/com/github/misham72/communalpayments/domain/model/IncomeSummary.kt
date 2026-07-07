package com.github.misham72.communalpayments.domain.model

data class IncomeSummary(
    val total: Double,
    val bySource: Map<IncomeCategory, Double>, // ключ — enum, а не строка
    val average: Double,
    val maxSource: IncomeCategory?,
    val minSource: IncomeCategory?
)
