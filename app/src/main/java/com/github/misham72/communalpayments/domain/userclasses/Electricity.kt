package com.github.misham72.communalpayments.domain.userclasses

import com.github.misham72.communalpayments.domain.calculators.MeterCalculator

// ✅ Чистый domain - нет Context, нет Android зависимостей!
class Electricity {

    data class ElectricityData(
//Data class — это чертёж (какие поля будут у объекта).
        val isHistory: Boolean,
        val current: Double,
        val previous: Double,
        val tariff: Double,
        val consumption: Double,
        val payment: Double,
        val accountNumber: String
    )

    fun collectElectricityData(
        //Функция — это действие, которое по этому чертежу создаёт реальный объект с конкретными числами.
        current: Double,
        previous: Double,
        tariff: Double,
        accountNumber: String
    ): ElectricityData {
        val result = MeterCalculator.calculate(current, previous, tariff)
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