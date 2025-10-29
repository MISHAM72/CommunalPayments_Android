package com.github.misham72.communalpayments.logic

import android.content.Context

class Gas(context: Context) : BaseService(context, "gas", "куб.м") {

    fun calculate(current: Double, previous: Double, tariff: Double): CalculationResult {
        val consumption = current - previous
        val payment = consumption * tariff

        saveCalculationResult(current, previous, tariff, consumption, payment)

        return CalculationResult(consumption, payment)
    }
}