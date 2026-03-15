package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.MeterCalculator

class Gas {

    data class GasData(
        val isHistory: Boolean, val current: Double, val previous: Double, val tariff: Double, val consumption: Double, val payment: Double, val accountNumber: String
    )

    fun collectGasData(                    //✅ Принимает сырые данные/
        current: Double, previous: Double, tariff: Double, accountNumber: String
    ): GasData {
        val result = MeterCalculator.calculate(current, previous, tariff)   // ✅ Запрашивает расчёты у калькулятора,
        return GasData(
            isHistory = true, current = current, previous = previous, tariff = tariff, consumption = result.consumption, payment = result.payment, accountNumber = accountNumber// ✅ Возвращает готовый, упакованный продукт

        )
    }
}