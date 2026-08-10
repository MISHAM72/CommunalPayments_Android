package com.github.misham72.communalpayments.domain.model.valueobjects

import com.github.misham72.communalpayments.domain.common.DomainMessages

@JvmInline
value class KilowattHour(val value: Double) {
    init {
        require(value >= 0.0) { DomainMessages.NEGATIVE_READING }
    }

    operator fun minus(other: KilowattHour): KilowattHour =
        KilowattHour(value - other.value)

    operator fun times(tariff: Tariff): Money =
        Money(value * tariff.value)
}
