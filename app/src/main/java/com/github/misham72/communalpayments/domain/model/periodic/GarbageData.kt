package com.github.misham72.communalpayments.domain.model.periodic

data class GarbageData(
    val serviceKey: String,
    val isHistory: Boolean,
    val nextPayment: String,
    val mode: GarbageMode,
    val value: Double,          // площадь или кол-во
    val priceTariff: Double,    // тариф за единицу
    val totalAmount: Double,    // итоговая сумма
    val periodMonths: Int,
    val accountNumber: String,
    val startDate: java.util.Date? = null
)
