package com.github.misham72.communalpayments.logic

import android.content.Context

class Electricity(context: Context) : BaseService(context, "electricity", "кВт·ч") {

    fun calculate(current: Double, previous: Double, tariff: Double): CalculationResult {
        val consumption = current - previous
        val payment = consumption * tariff

        // Автоматическое сохранение
        saveCalculationResult(current, previous, tariff, consumption, payment)

        return CalculationResult(consumption, payment)
    }

}