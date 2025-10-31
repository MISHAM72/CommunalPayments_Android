package com.github.misham72.communalpayments.logic

import android.content.Context

class Electricity(context: Context) : BaseService(context, "electricity", "кВт·ч") {

    fun calculate(current: Double, previous: Double, tariff: Double): CalculationResult {
        val consumption = current - previous
        val payment = consumption * tariff
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