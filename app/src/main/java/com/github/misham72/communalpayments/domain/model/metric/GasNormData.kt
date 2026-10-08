package com.github.misham72.communalpayments.domain.model.metric

data class GasNormData(
    val serviceKey: String,
    val isHistory: Boolean,
    val norm: Double,           // норматив м³/чел
    val people: Int,            // кол-во проживающих
    val tariff: Double,         // тариф ₽/м³
    val payment: Double,        // итоговая сумма
    val date: Long = System.currentTimeMillis()
)
