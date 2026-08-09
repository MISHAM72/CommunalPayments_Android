package com.github.misham72.communalpayments.domain.model

@Suppress("HardcodedStringLiteral")
object DomainMessages {
    const val NEGATIVE_CURRENT = "Текущее показание не может быть отрицательным"
    const val NEGATIVE_PREVIOUS = "Предыдущее показание не может быть отрицательным"
    const val CURRENT_LESS_THAN_PREVIOUS = "Текущее показание не может быть меньше предыдущего. Проверьте счётчик!"
    const val NEGATIVE_TARIFF = "Тариф не может быть отрицательным"
    const val DEFAULT_VALIDATION_ERROR = "Ошибка валидации показаний"
    const val NEGATIVE_READING = "Показание не может быть отрицательным"
}
