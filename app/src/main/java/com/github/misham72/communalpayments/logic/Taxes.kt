package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast

class Taxes(
    private val context: Context
) {
    private val fileManager = FileManager(context)

    data class TaxesData(
        val isHistory: Boolean,
        val formattedDateTime: String,
        val customStatus: String,
        val previousPayment: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Long
    )

    fun calculateTaxesData(): TaxesData {
        return TaxesData(
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

    fun saveTaxesData(data: TaxesData) {
        try {
            fileManager.formatPaymentDate(
                data.isHistory,
                "taxes",

                data.formattedDateTime,
                data.customStatus,
                data.previousPayment,
                data.nextPayment,
                data.daysFromPayment,
                data.daysUntilPayment,
                data.priceTariff

            )

            Toast.makeText(context, "Данные о налогах сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения Налоги: ${ex.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}
