package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast

class Troyka(private val context: Context) {
    private val fileManager = FileManager(context)

    data class TroykaData(
        val isHistory: Boolean,
        val formattedDateTime: String,
        val customStatus: String = "🔴 ОПЛАЧЕНО", // ← ДОБАВИТЬ ПОЛЕ
        val previousPayment: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Long,

        )

    fun calculateTroykaData(): TroykaData {
        return TroykaData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = "🔴 ОПЛАЧЕНО",
            previousPayment = DateCalculator.getPreviousPaymentString(12, 24),
            nextPayment = DateCalculator.getNextPaymentString(12, 24),
            daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(12, 24),
            daysUntilPayment = DateCalculator.calculateDaysToNextPayment(12, 24),
            priceTariff = 16400L,
        )
    }

    @Suppress("UNUSED")  // ← ДОБАВЬ ЭТУ СТРОКУ
    fun saveTroykaData(data: TroykaData) {
        try {
            fileManager.formatPaymentDate(
                data.isHistory,
                "troyka",
                data.formattedDateTime,
                data.customStatus,
                data.previousPayment,
                data.nextPayment,
                data.daysFromPayment,
                data.daysUntilPayment,
                data.priceTariff,

                )
            Toast.makeText(context, "Данные о карте Тройка сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(
                context, "Ошибка сохранения карта Тройка: ${ex.message}", Toast.LENGTH_LONG
            )
                .show()
        }
    }
}