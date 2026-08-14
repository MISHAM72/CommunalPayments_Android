package com.github.misham72.communalpayments.domain.common

@Suppress("HardcodedStringLiteral")
object DomainMessages {
    const val CURRENT_LESS_THAN_PREVIOUS = "Текущее показание не может быть меньше предыдущего. Проверьте счётчик!"
    const val DEFAULT_VALIDATION_ERROR = "Ошибка валидации показаний"
    const val NEGATIVE_READING = "Показание не может быть отрицательным"
    const val TARIFF_MUST_BE_POSITIVE = "Тариф должен быть положительным"
    const val AMOUNT_CANNOT_BE_NEGATIVE = "Сумма не может быть отрицательной"
    const val ACCOUNT_NUMBER_CANNOT_BE_EMPTY = "Номер счёта не может быть пустым"
    const val EXPECTED_ELECTRICITY_DATA = "Ожидается ElectricityData"
    const val EXPECTED_WATER_DATA = "Ожидается WaterData"
    const val EXPECTED_GAS_DATA = "Ожидается GasData"
    const val PERIOD_MUST_BE_POSITIVE = "Период должен быть больше нуля"
    const val DAY_OF_PAYMENTS_MUST_BE_FROM_1_TO_31 = "День платежа должен быть от 1 до 31"
}
