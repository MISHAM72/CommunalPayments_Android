package com.github.misham72.communalpayments.logic

import android.content.Context
import android.util.Log
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.logic.calculators.MeterCalculator


class Water(private val context: Context) {

    private val fileManager = FileManager(context)

    data class WaterData(

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

    fun collectWaterData(   //✅ Принимает сырые данные/
        current: Double,
        previous: Double,
        tariff: Double
    ): WaterData {
        val result = MeterCalculator.calculate(current, previous, tariff)   // ✅ Запрашивает расчёты у калькулятора, Зачем? разделить ответственность, отвечает  только за математику. Это делает код чище и переиспользуемым.

        return WaterData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = context.getString(R.string.status_paid),   //✅ Добавляет мета-информацию (дату, статус)
            current = current,
            previous = previous,
            tariff = tariff,
            consumption = result.consumption,  // ← из калькулятора
            payment = result.payment,          // ← из калькулятора
            unit = context.getString(R.string.unit_cubic_meter)
        )                                                                      // ✅ Возвращает готовый, упакованный продукт
    }

    fun saveWaterData(data: WaterData) {   // Получает готовый объект WaterData. Ваш saveWaterData — это "менеджер процесса", который собирает данные и передаёт их "исполнителю" (FileManager)
        val tag = context.getString(R.string.service_key_water)  // Короче: эта строка берёт значение "water" из файла ресурсов и сохраняет его в переменную tag, чтобы потом использовать для пометки логов.
        try {
            // 1. ПОДГОТОВКА ВСЕХ ГОТОВЫХ СТРОК:Берёт данные из объекта и форматирует их в читаемые строки
            val readyHeader = if (data.isHistory) "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩" else ""
            val serviceName = context.getString(R.string.service_display_name_water)
            val readyService = context.getString(R.string.custom_ready_service, serviceName)
            val readySeparator1 = "----------------------------------------------------------"
            val readyDateTime = "( ${data.formattedDateTime} )"
            val readyStatus = if (data.customStatus.isNotEmpty()) context.getString(R.string.custom_status_paid, data.customStatus) else ""
            val readySeparator2 = "----------------------------------------------------------"   // Разделитель
            val readyCurrentReading = context.getString(R.string.current_reading, context.getString(R.string.format_two_decimals).format(data.current), data.unit)
            val readyPreviousReading = context.getString(R.string.previous_reading, context.getString(R.string.format_two_decimals).format(data.previous), data.unit)
            val readyTariff = context.getString(R.string.tariff, data.tariff)
            val readyConsumption = context.getString(R.string.consumption, context.getString(R.string.format_two_decimals).format(data.consumption), data.unit)
            val readyPaymentSum = context.getString(R.string.payment_sum, context.getString(R.string.format_two_decimals).format(data.payment))
            val fileName = fileManager.getFileName(tag)

            // 2. ВЫЗОВ ФУНКЦИИ:
            fileManager.saveMeterPayment(   // 4. СОХРАНЕНИЕ: Передаёт ВСЕ готовые строки в fileManager
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
