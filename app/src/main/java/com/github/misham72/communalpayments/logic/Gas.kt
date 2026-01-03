package com.github.misham72.communalpayments.logic

import android.content.Context
import android.util.Log
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.logic.calculators.MeterCalculator

class Gas(private val context: Context) {

    private val fileManager = FileManager(context)

    data class GasData(
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

    fun collectGasData(                                                    //✅ Принимает сырые данные/
        current: Double, previous: Double, tariff: Double
    ): GasData {
        val result = MeterCalculator.calculate(current, previous, tariff)   // ✅ Запрашивает расчёты у калькулятора,

        return GasData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = context.getString(R.string.status_paid),    //✅ Добавляет мета-информацию (дату, статус)
            current = current,
            previous = previous,
            tariff = tariff,
            consumption = result.consumption,
            payment = result.payment,          // ← из калькулятора
            unit = context.getString(R.string.unit_cubic_meter)
        )                                                                       // ✅ Возвращает готовый, упакованный продукт
    }

    fun saveGasData(data: GasData) {
        val tag = context.getString(R.string.service_key_gas) // Короче: эта строка берёт значение "gas" из файла ресурсов и сохраняет его в переменную tag, чтобы потом использовать для пометки логов.

        try {

            val readyHeader = if (data.isHistory) "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩" else ""    // a) Заголовок (🟩🟩🟩... или пусто)
            val serviceName = context.getString(R.string.service_display_name_gas)    // Текст на вкладке для пользователя - Свет.
            val readyService = context.getString(R.string.custom_ready_service, serviceName)   // Это шаблон "Услуга - %s", а serviceName - это значение "Свет", которое встанет на место %s.
            val readySeparator1 = "----------------------------------------------------------"   // c) Разделитель
            val readyDateTime = "( ${data.formattedDateTime} )"   // d) Дата в скобках
            val readyStatus = if (data.customStatus.isNotEmpty()) context.getString(R.string.custom_status_paid, data.customStatus) else ""  // e) Статус для пользователя (если есть)
            val readySeparator2 = "----------------------------------------------------------"   // c) Разделитель
            val readyCurrentReading = context.getString(R.string.current_reading, context.getString(R.string.format_two_decimals).format(data.current), data.unit)
            val readyPreviousReading = context.getString(R.string.previous_reading, context.getString(R.string.format_two_decimals).format(data.previous), data.unit)
            val readyTariff = context.getString(R.string.tariff, context.getString(R.string.format_two_decimals).format(data.tariff))
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

            Log.i(tag, "✅ " + context.getString(R.string.data_saved))
        } catch (e: Exception) {
            // 4. ОШИБКА
            Log.e(tag, "❌ " + context.getString(R.string.error_saving), e)
        }
    }
}
