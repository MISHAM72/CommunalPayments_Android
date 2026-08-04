package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.MeterCalculator
import com.github.misham72.communalpayments.domain.model.ElectricityData

// ✅ Чистый domain - нет Context, нет Android зависимостей!
class Electricity(private val meterCalculator: MeterCalculator) {

    fun collectElectricityData(
        //Функция — это действие, которое по этому чертежу создаёт реальный объект с конкретными числами.
        current: Double,
        previous: Double,
        tariff: Double,
        accountNumber: String
    ): ElectricityData {
        val result = meterCalculator.calculate(current, previous, tariff)
        return ElectricityData(
            //Когда функция пишет return ElectricityData(...), она создаёт конкретный экземпляр этого data class, заполняя поля переданными значениями.
            isHistory = true,  // Для новых показаний isHistory = true
            current = current,
            previous = previous,
            tariff = tariff,
            consumption = result.consumption,
            payment = result.payment,
            accountNumber = accountNumber
        )
    }
}
