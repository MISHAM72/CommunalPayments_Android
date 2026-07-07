package com.github.misham72.communalpayments.domain.model

import java.util.Date

data class TroykaData(
    val isHistory: Boolean,
    val previousPayment: String,
    val nextPayment: String,
    val daysFromPayment: Long,
    val daysUntilPayment: Long,
    val priceTariff: Double,
    val periodMonths: String,
    val accountNumber: String,
    val startDate: Date?
)
