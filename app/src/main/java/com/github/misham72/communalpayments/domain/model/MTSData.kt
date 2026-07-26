package com.github.misham72.communalpayments.domain.model

import java.util.Date

data class MTSData(
    val isHistory: Boolean,
    val nextPayment: String,
    val priceTariff: Double,
    val periodMonths: Int,
    val accountNumber: String,
    val startDate: Date?
)
