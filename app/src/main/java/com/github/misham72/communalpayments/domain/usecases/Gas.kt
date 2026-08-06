package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.calculators.MeterCalculator
import com.github.misham72.communalpayments.domain.model.GasData

class Gas (private val meterCalculator: MeterCalculator){

    fun collectGasData(                    //✅ Принимает сырые данные/
        current: Double, previous: Double, tariff: Double, accountNumber: String
    ): GasData {
        val result = meterCalculator.calculate(current, previous, tariff)   // ✅ Запрашивает расчёты у калькулятора,
        return GasData(
            isHistory = true, current = current, previous = previous, tariff = tariff, consumption = result.consumption, payment = result.payment, accountNumber = accountNumber// ✅ Возвращает готовый, упакованный продукт

        )
    }
}
