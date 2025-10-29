package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Taxes(
    private val context: Context
) {
    private val fileManager = FileManager(context)


    data class TaxesData(
        val daysUntilPayment: Long,
        val daysFromPayment: Long,
        val nextPayment: String,
        val previousPayment: String,
        val priceTariff: Long,
        val currentDate: String,
        val formattedDateTime: String,
        val nextPaymentDate: Date,
        val previousPaymentDate: Date

    )

    fun calculateTaxesData(): TaxesData {
        val daysUntilPayment = DateCalculator.calculateDaysToNextPayment(1, 30)
        val daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(1, 30)

        // Получаем даты как строки
        val nextPayment = getFutureDateString(daysUntilPayment)
        val previousPayment = getPastDateString(daysFromPayment)
        val currentDate = getCurrentDateString()

        // ДОБАВЬТЕ ЭТИ СТРОКИ - получаем объекты Date
        val nextPaymentDate = DateCalculator.getNextPaymentDate(1, 30)
        val previousPaymentDate = DateCalculator.getPreviousPaymentDate(1, 30)

        val priceTariff = 13000L
        val formattedDateTime =
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        return TaxesData(
            daysUntilPayment,
            daysFromPayment,
            nextPayment,
            previousPayment,
            priceTariff,
            currentDate,
            formattedDateTime,
            nextPaymentDate,
            previousPaymentDate
        )
    }

    private fun getCurrentDateString(): String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    }

    private fun getFutureDateString(daysToAdd: Long): String {
        val date = Date()
        date.time = date.time + daysToAdd * 24 * 60 * 60 * 1000L
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
    }


    private fun getPastDateString(daysToSubtract: Long): String {
        val date = Date()
        date.time = date.time - daysToSubtract * 24 * 60 * 60 * 1000L
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
    }

    @Suppress("UNUSED")  // ← ДОБАВЬ ЭТУ СТРОКУ
    fun saveTaxesData(data: TaxesData) {
        saveTaxesData(data, "") // вызываем перегруженный метод с пустым статусом
    }

    fun saveTaxesData(data: TaxesData, customStatus: String) {
        try {
            fileManager.formatPaymentDate(
                "taxes",
                data.daysUntilPayment,
                data.daysFromPayment,
                data.nextPayment,
                data.previousPayment,
                data.priceTariff,
                data.formattedDateTime,
                data.nextPaymentDate,
                data.previousPaymentDate,
                customStatus             // ← customStatus: String
            )

            Toast.makeText(context, "Данные о налогах сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения Налоги: ${ex.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}
