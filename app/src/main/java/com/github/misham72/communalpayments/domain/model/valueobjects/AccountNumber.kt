package com.github.misham72.communalpayments.domain.model.valueobjects

import com.github.misham72.communalpayments.domain.common.DomainMessages

@JvmInline
value class AccountNumber(val value: String) {
    init {
        require(value.isNotBlank()) { DomainMessages.ACCOUNT_NUMBER_CANNOT_BE_EMPTY }
        // можно добавить проверку формата, например, length == 12
    }
}
