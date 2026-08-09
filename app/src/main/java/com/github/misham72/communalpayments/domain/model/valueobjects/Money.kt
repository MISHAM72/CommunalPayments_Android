package com.github.misham72.communalpayments.domain.model.valueobjects

import com.github.misham72.communalpayments.domain.model.DomainMessages

@JvmInline
value class Money(val amount: Double) {
    init {
        require(amount >= 0.0) { DomainMessages.AMOUNT_CANNOT_BE_NEGATIVE }
    }

    operator fun plus(other: Money): Money = Money(amount + other.amount)
    operator fun minus(other: Money): Money = Money(amount - other.amount)
}
