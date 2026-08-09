package com.github.misham72.communalpayments.domain.model.valueobjects

@JvmInline
value class Money(val amount: Double) {
    init {
        require(amount >= 0.0) { "Сумма не может быть отрицательной" }
    }

    operator fun plus(other: Money): Money = Money(amount + other.amount)
    operator fun minus(other: Money): Money = Money(amount - other.amount)
}
