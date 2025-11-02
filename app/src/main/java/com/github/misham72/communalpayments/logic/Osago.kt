package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast

class Osago(private val context: Context) {
    private val fileManager = FileManager(context)


    data class OsagoData(
        val isHistory: Boolean,
        val formattedDateTime: String,
        val customStatus: String = "🔴 ОПЛАЧЕНО", // ← ДОБАВИТЬ ПОЛЕ
        val previousPayment: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Long
    )

    fun calculateOsagoData(): OsagoData {
        return OsagoData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = "🔴 ОПЛАЧЕНО",
            previousPayment = DateCalculator.getPreviousPaymentString(12, 27),
            nextPayment = DateCalculator.getNextPaymentString(12, 27),
            daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(12, 27),
            daysUntilPayment = DateCalculator.calculateDaysToNextPayment(12, 27),
            priceTariff = 7530L,
        )
    }


    fun saveOsagoData(data: OsagoData) {
        try {
            fileManager.formatPaymentDate(
                data.isHistory,
                "osago",
                data.formattedDateTime,
                data.customStatus,
                data.previousPayment,
                data.nextPayment,
                data.daysFromPayment,
                data.daysUntilPayment,
                data.priceTariff
            )

            Toast.makeText(context, "Данные о полисе ОСАГО сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(
                context, "Ошибка сохранения полиса ОСАГО: ${ex.message}", Toast.LENGTH_LONG
            ).show()
        }
    }
}




