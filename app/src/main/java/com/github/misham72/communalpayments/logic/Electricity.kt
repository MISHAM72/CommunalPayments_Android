package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast

class Electricity(private val context: Context) {

    private val fileManager = FileManager(context)

    data class ElectricityData(
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

    fun calculateElectricityData(
        current: Double,
        previous: Double,
        tariff: Double
    ): ElectricityData {
        val result = MeterCalculator.calculate(current, previous, tariff)

        return ElectricityData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = "🔴 ОПЛАЧЕНО",
            current = current,
            previous = previous,
            tariff = tariff,
            consumption = result.consumption,  // ← из калькулятора
            payment = result.payment,          // ← из калькулятора
            unit = "кВт/ч"
        )
    }

    fun saveElectricityData(data: ElectricityData) {
        try {
            fileManager.formatMeterReadingPaymentData(
                data.isHistory,
                "electricity",
                data.formattedDateTime,
                data.customStatus,
                data.current,
                data.previous,
                data.consumption,
                data.tariff,
                data.payment,
                data.unit
            )

            Toast.makeText(context, "Данные по электричеству сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }
}