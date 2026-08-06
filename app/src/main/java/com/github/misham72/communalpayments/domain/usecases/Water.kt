package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.calculators.MeterCalculator
import com.github.misham72.communalpayments.domain.model.WaterData

class Water(private val meterCalculator: MeterCalculator) {
    fun collectWaterData(   //✅ Принимает сырые данные/
        current: Double,
        previous: Double,
        tariff: Double,
        accountNumber: String
    ): WaterData {
        val result = meterCalculator.calculate(current, previous, tariff)   // ✅ Запрашивает расчёты у калькулятора, Зачем? Разделить ответственность, отвечает только за математику. Это делает код чище и переиспользуемым.

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
