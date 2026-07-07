package com.github.misham72.communalpayments.domain.model

data class ExpenseSummary(
val total: Double,
val byService: Map<String, Double> // ключ – название услуги (или enum)
)
