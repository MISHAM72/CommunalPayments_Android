package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast

class Internet(private val context: Context) {
    private val fileManager = FileManager(context)

    data class InternetData(
        val isHistory: Boolean,
        val formattedDateTime: String,
        val customStatus: String,
        val previousPayment: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Long
    )

    fun calculateInternetData(): InternetData {

        return InternetData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = "🔴 ОПЛАЧЕНО",
            previousPayment = DateCalculator.getPreviousPaymentString(1, 30),
            nextPayment = DateCalculator.getNextPaymentString(1, 30),
            daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(1, 30),
            daysUntilPayment = DateCalculator.calculateDaysToNextPayment(1, 30),
            priceTariff = 950L,
        )
    }

    fun saveInternetData(data: InternetData) {
        try {
            fileManager.formatPaymentDate(
                data.isHistory,
                "internet",
                data.formattedDateTime,
                data.customStatus,
                data.previousPayment,
                data.nextPayment,
                data.daysFromPayment,
                data.daysUntilPayment,
                data.priceTariff
            )

            Toast.makeText(context, "Данные Интернет сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения Интернет: ${ex.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}

