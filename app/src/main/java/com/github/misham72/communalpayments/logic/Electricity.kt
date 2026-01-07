package com.github.misham72.communalpayments.logic

import android.content.Context
import android.util.Log
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.logic.calculators.MeterCalculator

class Electricity(private val context: Context) {

    private val fileManager = FileManager(context)

    data class ElectricityData(      //один удобный контейнер.
        val isHistory: Boolean,
        val formattedDateTime: String,
        val customStatus: String,
        val current: Double,
        val previous: Double,
        val tariff: Double,
        val consumption: Double,
        val payment: Double,
        val unit: String
    )

    fun collectElectricityData(  //✅ Принимает сырые данные/
        current: Double,           // объявление метода: "функция,
        previous: Double,          //  которая принимает три числа,
        tariff: Double
    ): ElectricityData {           // и возвращает объект ElectricityData".
        val result = MeterCalculator.calculate(current, previous, tariff)  // ✅ Запрашивает расчёты у калькулятора, Зачем? разделить ответственность, отвечает  только за математику. Это делает код чище и переиспользуемым.

        return ElectricityData(   //Зачем? Чтобы упаковать ВСЮ информацию в один удобный контейнер. СБОРКА полного объекта ←─ добавление даты, статуса, единиц
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = context.getString(R.string.status_paid),   //✅ Добавляет мета-информацию (дату, статус)
            current = current,
            previous = previous,
            tariff = tariff,
            consumption = result.consumption,  // ← из калькулятора
            payment = result.payment,          // ← из калькулятора
            unit = context.getString(R.string.unit_kilowatt_hour)      // ✅ Возвращает готовый, упакованный продукт (ElectricityData)
        )
    }

    fun saveElectricityData(data: ElectricityData) {
        val tag = context.getString(R.string.service_key_electricity) // Короче: эта строка берёт значение "electricity" из файла ресурсов и сохраняет его в переменную tag, чтобы потом использовать для пометки логов.

        try {
            // 1. ПОДГОТОВКА ВСЕХ ГОТОВЫХ СТРОК:

            val readyHeader = if (data.isHistory) "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩" else ""   //  Если запись историческая (data.isHistory == true), то заголовком будет строка из "🟩🟩🟩". Если нет — заголовок будет пустым
            val serviceName = (context.getString(R.string.service_display_name_electricity))  //  Текст на вкладке для пользователя - Свет.
            val readyService = context.getString(R.string.custom_ready_service, serviceName)  // Это шаблон "Услуга - %s", а serviceName - это значение "Свет", которое встанет на место %s.
            val readySeparator1 = "----------------------------------------------------------"   // Разделитель.
            val readyDateTime = "( ${data.formattedDateTime} )"   // d) Дата в скобках
            val readyStatus = if (data.customStatus.isNotEmpty()) context.getString(R.string.custom_status_paid, data.customStatus) else ""  // e) Статус для пользователя (если есть)
            val readySeparator2 = "----------------------------------------------------------"   // Разделитель.
            val readyCurrentReading = context.getString(R.string.current_reading, context.getString(R.string.format_two_decimals).format(data.current), data.unit)
            val readyPreviousReading = context.getString(R.string.previous_reading, context.getString(R.string.format_two_decimals).format(data.previous), data.unit)
            val readyTariff = context.getString(R.string.tariff, data.tariff)
            val readyConsumption = context.getString(R.string.consumption, context.getString(R.string.format_two_decimals).format(data.consumption), data.unit)
            val readyPaymentSum = context.getString(R.string.payment_sum, context.getString(R.string.format_two_decimals).format(data.payment))
            val fileName = fileManager.getFileName(tag)

            // 2. ВЫЗОВ ФУНКЦИИ:
            fileManager.saveMeterPayment(
                readyHeader = readyHeader,
                readyService = readyService,
                readySeparator1 = readySeparator1,
                readyDateTime = readyDateTime,
                readyStatus = readyStatus,
                readySeparator2 = readySeparator2,
                readyCurrentReading = readyCurrentReading,
                readyPreviousReading = readyPreviousReading,
                readyTariff = readyTariff,
                readyConsumption = readyConsumption,
                readyPaymentSum = readyPaymentSum,
                fileName = fileName
            )

        } catch (e: Exception) {
            // 4. ОШИБКА
            Log.e(tag, "❌ " + context.getString(R.string.error_saving), e)
        }
    }
}