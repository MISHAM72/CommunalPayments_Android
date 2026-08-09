package com.github.misham72.communalpayments.domain.model.valueobjects

@JvmInline
value class AccountNumber(val value: String) {
    init {
        require(value.isNotBlank()) { "Номер счёта не может быть пустым" }
        // можно добавить проверку формата, например, length == 12
    }
}
