package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast


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

    fun calculateWaterData(
        current: Double,
        previous: Double,
        tariff: Double
    ): WaterData {
        val result = MeterCalculator.calculate(current, previous, tariff)

        return WaterData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = "🔴 ОПЛАЧЕНО",
            current = current,
            previous = previous,
            tariff = tariff,
            consumption = result.consumption,  // ← из калькулятора
            payment = result.payment,          // ← из калькулятора
            unit = "куб.м"
        )
    }

    fun saveWaterData(data: WaterData) {
        try {
            fileManager.formatMeterReadingPaymentData(
                data.isHistory,
                "water",
                data.formattedDateTime,
                data.customStatus,
                data.current,
                data.previous,
                data.consumption,
                data.tariff,
                data.payment,
                data.unit
            )

            Toast.makeText(context, "Данные по водоснабжению сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(
                context,
                "Ошибка сохранения в услуге - вода: ${ex.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
