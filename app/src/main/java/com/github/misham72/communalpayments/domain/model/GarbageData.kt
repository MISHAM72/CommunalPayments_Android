package com.github.misham72.communalpayments.domain.model

import java.util.Date

data class GarbageData(
    val isHistory: Boolean,
    val previousPayment: String,
    val daysFromPayment: Long,
    val nextPayment: String,
    val daysUntilPayment: Long,
    val priceTariff: Double,
    val periodMonths: String,
    val accountNumber: String,
    val startDate: Date?
)
