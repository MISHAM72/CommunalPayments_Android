package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast

class MTS(private val context: Context) {

    private val fileManager = FileManager(context)

    data class MTSData(
        val isHistory: Boolean,
        val formattedDateTime: String,
        val previousPayment: String,
        val customStatus: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Long
    )

    fun calculateMTSData(): MTSData {
        return MTSData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = "🔴 ОПЛАЧЕНО",
            previousPayment = DateCalculator.getPreviousPaymentString(1, 23),
            nextPayment = DateCalculator.getNextPaymentString(1, 23),
            daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(1, 23),
            daysUntilPayment = DateCalculator.calculateDaysToNextPayment(1, 23),
            priceTariff = 402L,
        )
    }


    fun saveMTSData(data: MTSData) {
        try {
            fileManager.formatPaymentDate(
                data.isHistory,
                "mts",

                data.formattedDateTime,
                data.customStatus,
                data.previousPayment,
                data.nextPayment,
                data.daysFromPayment,
                data.daysUntilPayment,
                data.priceTariff
            )

            Toast.makeText(context, "Данные МТС сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения МТС: ${ex.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}