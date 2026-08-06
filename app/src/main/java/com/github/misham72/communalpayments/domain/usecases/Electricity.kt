package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.calculators.MeterCalculator
import com.github.misham72.communalpayments.domain.model.ElectricityData

// ✅«Собираем данные и создаем объект»
//В программировании это действие называется «Сборка DTO (Data Transfer Object)» или «Фабричный метод».
class Electricity(private val meterCalculator: MeterCalculator) {
    //Функция — это действие, которое по этому чертежу создаёт реальный объект с конкретными числами.
    fun collectElectricityData(
        current: Double,//Это строгая типизация. Ты говоришь: «Сюда можно передавать только числа, а строки нельзя».
        previous: Double,
        tariff: Double,
        accountNumber: String
    ): ElectricityData {//Это контракт. Ты говоришь компилятору: «Я обещаю, что в конце этой функции я обязательно соберу и отдам наружу объект именно такой структуры (ElectricityData)».
        val result = meterCalculator.calculate(current, previous, tariff)//«создаем переменную с результатом» (✅ Абсолютно верно!)
        return ElectricityData(//Ты просто упаковываешь их в объект ElectricityData, чтобы передать этот "пакет" дальше (в Репозиторий).
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
