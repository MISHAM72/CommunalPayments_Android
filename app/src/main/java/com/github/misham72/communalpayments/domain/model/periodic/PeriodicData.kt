package com.github.misham72.communalpayments.domain.model.periodic

import java.util.Date

data class PeriodicData(
    val serviceKey: String,          // уникальный ключ услуги
    val isHistory: Boolean,
    val nextPayment: String,
    val priceTariff: Double,
    val periodMonths: Int,
    val accountNumber: String,
    val startDate: Date? = null
)
