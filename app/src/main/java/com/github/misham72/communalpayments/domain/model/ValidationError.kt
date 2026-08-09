package com.github.misham72.communalpayments.domain.model

sealed class ValidationError {
    object InvalidInput : ValidationError()
    object SavingError : ValidationError()
    data class DomainError(val message: String) : ValidationError()
    // можно добавить другие типы ошибок, например:
    // object EmptyField, object NegativeValue и т.д.
}
