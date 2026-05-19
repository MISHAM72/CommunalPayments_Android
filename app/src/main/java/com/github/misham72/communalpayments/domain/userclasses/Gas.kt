package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.MeterCalculator
import com.github.misham72.communalpayments.domain.model.GasData

class Gas {

    fun collectGasData(                    //✅ Принимает сырые данные/
        current: Double, previous: Double, tariff: Double, accountNumber: String
    ): GasData {
        val result = MeterCalculator.calculate(current, previous, tariff)   // ✅ Запрашивает расчёты у калькулятора,
        return GasData(
            isHistory = true, current = current, previous = previous, tariff = tariff, consumption = result.consumption, payment = result.payment, accountNumber = accountNumber// ✅ Возвращает готовый, упакованный продукт

        )
    }
}
