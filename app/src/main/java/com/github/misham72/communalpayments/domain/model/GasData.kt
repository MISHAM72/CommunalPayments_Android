package com.github.misham72.communalpayments.domain.model

data class GasData(
    val isHistory: Boolean,
    val current: Double,
    val previous: Double,
    val tariff: Double,
    val consumption: Double,
    val payment: Double,
    val accountNumber: String
)
