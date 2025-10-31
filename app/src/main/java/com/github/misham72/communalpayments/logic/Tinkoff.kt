package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Tinkoff(private val context: Context) {


    private val fileManager = FileManager(context)

    data class TinkoffData(
        val formattedDateTime: String,
        val previousPayment: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Long,
    )

    fun calculateTinkoffData(): TinkoffData {
        val formattedDateTime = fileManager.getCurrentDateTime() // ← ПРАВИЛЬНО!

        val daysUntilPayment = DateCalculator.calculateDaysToNextPayment(1, 23)
        val daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(1, 23)

        val previousPayment = getPastDateString(daysFromPayment)  // ← "30.10.2023"
        val nextPayment = getFutureDateString(daysUntilPayment)   // ← "30.11.2023"
        val priceTariff = 402L

        return TinkoffData(
            formattedDateTime,
            previousPayment,
            nextPayment,
            daysFromPayment,
            daysUntilPayment,
            priceTariff,
        )
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
    // Обычное сохранение
    fun saveTinkoffData(data: TinkoffData) {
        saveTinkoffData(data, "") // вызываем перегруженный метод с пустым статусом
    }

    fun saveTinkoffData(data: TinkoffData, customStatus: String) {
        try {
            fileManager.formatPaymentDate(
                "tinkoff",
                data.formattedDateTime,
                customStatus = customStatus,
                data.previousPayment,
                data.nextPayment,
                data.daysFromPayment,
                data.daysUntilPayment,
                data.priceTariff,


                )

            Toast.makeText(context, "Данные Тинькоф сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения Тинькоф: ${ex.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}
