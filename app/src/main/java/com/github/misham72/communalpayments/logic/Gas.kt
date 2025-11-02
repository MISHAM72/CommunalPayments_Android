package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast

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

    fun calculateGasData(
        current: Double, previous: Double, tariff: Double
    ): GasData {
        val result = MeterCalculator.calculate(current, previous, tariff)

        return GasData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = "🔴 ОПЛАЧЕНО",
            current = current,
            previous = previous,
            tariff = tariff,
            consumption = result.consumption,
            payment = result.payment,          // ← из калькулятора
            unit = "куб.м"
        )
    }

    fun saveGasData(data: GasData) {
        try {
            fileManager.formatMeterReadingPaymentData(
                data.isHistory,
                "gas",
                data.formattedDateTime,
                data.customStatus,
                data.current,
                data.previous,
                data.consumption,
                data.tariff,
                data.payment,
                data.unit
            )

            Toast.makeText(context, "Данные по газоснабжению сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(
                context, "Ошибка сохранения в услуге - газ: ${ex.message}", Toast.LENGTH_LONG
            ).show()
        }
    }
}
