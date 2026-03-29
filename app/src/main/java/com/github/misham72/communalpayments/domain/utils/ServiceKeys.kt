package com.github.misham72.communalpayments.domain.utils


object ServiceKeys {
    const val ELECTRICITY = "electricity"
    const val GAS = "gas"
    const val WATER = "water"
    const val ZONT = "zont"
    const val INTERNET = "internet"
    const val MTS = "mts"
    const val TINKOFF = "tinkoff"
    const val GARBAGE = "garbage"
    const val TAXES = "taxes"
    const val TROYKA = "troyka"
    const val OSAGO = "osago"

    // периоды оплаты в днях (можно использовать в будущем для цветов, уведомлений)
    val periodDays = mapOf(
        ELECTRICITY to 30, GAS to 30, WATER to 30, ZONT to 30, INTERNET to 30, MTS to 30, TINKOFF to 30, GARBAGE to 30, TAXES to 365, TROYKA to 30, OSAGO to 365
    )
}