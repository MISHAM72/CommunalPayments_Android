package com.github.misham72.communalpayments.domain.utils

@Suppress("HardcodedStringLiteral")
object ServiceKeys {
    const val ELECTRICITY: String = "electricity"
    const val GAS: String = "gas"
    const val WATER: String = "water"
    const val ZONT: String = "zont"
    const val INTERNET: String = "internet"
    const val MTS: String = "mts"
    const val TINKOFF: String = "tinkoff"
    const val GARBAGE: String = "garbage"
    const val TAXES: String = "taxes"
    const val TROYKA: String = "troyka"
    const val OSAGO: String = "osago"

    // периоды оплаты в днях (можно использовать в будущем для цветов, уведомлений)
    val periodDays: Map<String, Int> = mapOf(
        ELECTRICITY to 30, GAS to 30, WATER to 30, ZONT to 30, INTERNET to 30, MTS to 30, TINKOFF to 30, GARBAGE to 30, TAXES to 365, TROYKA to 30, OSAGO to 365
    )
}