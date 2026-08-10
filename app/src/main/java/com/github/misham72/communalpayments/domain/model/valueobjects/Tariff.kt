package com.github.misham72.communalpayments.domain.model.valueobjects

import com.github.misham72.communalpayments.domain.common.DomainMessages

@JvmInline
value class Tariff(val value: Double) {
    init {
        require(value > 0.0) { DomainMessages.TARIFF_MUST_BE_POSITIVE }
    }
}
