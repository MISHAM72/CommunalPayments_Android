package com.github.misham72.communalpayments.logic

data class MeterCalculator(

    val consumption: Double, val payment: Double
) {
    companion object {
        fun calculate(current: Double, previous: Double, tariff: Double): MeterCalculator {
            val consumption = current - previous
            val payment = consumption * tariff
            return MeterCalculator(consumption, payment)
        }
    }
}