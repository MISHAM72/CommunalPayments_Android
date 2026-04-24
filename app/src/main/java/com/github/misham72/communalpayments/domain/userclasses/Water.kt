package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.MeterCalculator

class Water {
    data class WaterData(

        val isHistory: Boolean,
        val current: Double,
        val previous: Double,
        val tariff: Double,
        val consumption: Double,
        val payment: Double,
        val accountNumber: String,
    )

    fun collectWaterData(   //✅ Принимает сырые данные/
        current: Double,
        previous: Double,
        tariff: Double,
        accountNumber: String
    ): WaterData {
        val result = MeterCalculator.calculate(current, previous, tariff)   // ✅ Запрашивает расчёты у калькулятора, Зачем? Разделить ответственность, отвечает только за математику. Это делает код чище и переиспользуемым.

        return WaterData(
            isHistory = true,
            current = current,
            previous = previous,
            tariff = tariff,
            consumption = result.consumption,  // ← из калькулятора
            payment = result.payment,
            accountNumber = accountNumber
        )                                                                      // ✅ Возвращает готовый, упакованный продукт
    }
}