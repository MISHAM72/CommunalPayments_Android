package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast

class Garbage(private val context: Context) {

    private val fileManager = FileManager(context)

    data class GarbageData(
        val isHistory: Boolean,
        val formattedDateTime: String,
        val customStatus: String,
        val previousPayment: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Long

    )

    fun calculateGarbageData(): GarbageData {
        return GarbageData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = "🔴 ОПЛАЧЕНО",
            previousPayment = DateCalculator.getPreviousPaymentString(1, 25),
            nextPayment = DateCalculator.getNextPaymentString(1, 25),
            daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(1, 25),
            daysUntilPayment = DateCalculator.calculateDaysToNextPayment(1, 25),
            priceTariff = 100L,
        )
    }

    @Suppress("UNUSED")
    fun saveGarbageData(data: GarbageData) {
        try {
            fileManager.formatPaymentDate(
                data.isHistory,
                "garbage",

                data.formattedDateTime,
                data.customStatus,
                data.previousPayment,
                data.nextPayment,
                data.daysFromPayment,
                data.daysUntilPayment,
                data.priceTariff

            )

            Toast.makeText(context, "Данные Мусор сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения Мусор: ${ex.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}