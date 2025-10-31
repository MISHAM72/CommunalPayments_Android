package com.github.misham72.communalpayments.logic

import android.content.Context

class Gas(context: Context) : BaseService(context, "gas", "куб.м") {

    fun calculate(current: Double, previous: Double, tariff: Double): CalculationResult {
        val consumption = current - previous
        val payment = consumption * tariff
        // 👇 ДОБАВЬТЕ ЦВЕТНОЙ СТАТУС:
        saveCalculationResult(
            current = current,
            previous = previous,
            tariff = tariff,
            consumption = consumption,
            payment = payment,
            customStatus = "<font color='#FF0000'>🔴 ОПЛАЧЕНО</font>" // ← ДОБАВИТЬ ЭТУ СТРОКУ
        )

        return CalculationResult(consumption, payment)
    }
}