package com.github.misham72.communalpayments.domain.model.valueobjects

@JvmInline
value class Tariff(val value: Double) {
    init {
        require(value > 0.0) { "Тариф должен быть положительным" }
    }
}
