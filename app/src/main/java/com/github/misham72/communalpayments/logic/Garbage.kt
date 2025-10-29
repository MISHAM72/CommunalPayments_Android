package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Garbage(private val context: Context) {

    private val fileManager = FileManager(context)

    data class GarbageData(
        val daysUntilPayment: Long,
        val daysFromPayment: Long,
        val nextPayment: String,
        val previousPayment: String,
        val priceTariff: Long,
        val currentDate: String,
        val formattedDateTime: String,  // ← formattedDateTime ДОЛЖЕН БЫТЬ ПЕРЕД Date!
        val nextPaymentDate: Date,
        val previousPaymentDate: Date
    )

    fun calculateGarbageData(): GarbageData {
        val daysUntilPayment = DateCalculator.calculateDaysToNextPayment(1, 23)
        val daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(1, 23)

        // Получаем даты как строки
        val nextPayment = getFutureDateString(daysUntilPayment)
        val previousPayment = getPastDateString(daysFromPayment)
        val currentDate = getCurrentDateString()

        // Получаем объекты Date
        val nextPaymentDate = DateCalculator.getNextPaymentDate(1, 23)
        val previousPaymentDate = DateCalculator.getPreviousPaymentDate(1, 23)

        val priceTariff = 402L
        val formattedDateTime =
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        return GarbageData(
            daysUntilPayment,
            daysFromPayment,
            nextPayment,
            previousPayment,
            priceTariff,
            currentDate,
            formattedDateTime,  // ← formattedDateTime ПЕРЕД Date объектами
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
    // Обычное сохранение
    fun saveGarbageData(data: GarbageData) {
        saveGarbageData(data, "") // вызываем перегруженный метод с пустым статусом
    }

    @Suppress("UNUSED")  // ← ДОБАВЬ ЭТУ СТРОКУ
    // Перегруженный метод с кастомным статусом
    fun saveGarbageData(data: GarbageData, customStatus: String) {
        try {
            fileManager.formatPaymentDate(
                "garbage",
                data.daysUntilPayment,
                data.daysFromPayment,
                data.nextPayment,
                data.previousPayment,
                data.priceTariff,
                data.formattedDateTime,  // ← formattedDateTime: String
                data.nextPaymentDate,    // ← nextPaymentDate: Date
                data.previousPaymentDate, // ← previousPaymentDate: Date
                customStatus             // ← customStatus: String
            )

            Toast.makeText(context, "Данные Мусор сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения Мусор: ${ex.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}