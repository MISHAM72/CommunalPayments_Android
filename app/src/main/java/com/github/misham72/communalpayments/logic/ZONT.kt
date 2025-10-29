package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ZONT(private val context: Context?) {

    private val fileManager = FileManager(context!!)

    data class ZONTData(
        val daysUntilPayment: Long,
        val daysFromPayment: Long,
        val nextPayment: String,
        val previousPayment: String,
        val priceTariff: Long,
        val currentDate: String,
        val formattedDateTime: String, // ← formattedDateTime ДОЛЖЕН БЫТЬ ПЕРЕД Date!
        val nextPaymentDate: Date,
        val previousPaymentDate: Date

    )

    fun calculateZONTData(): ZONTData {
        val daysUntilPayment = DateCalculator.calculateDaysToNextPayment(1, 30)
        val daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(1, 30)

        // Получаем даты как строки
        val nextPayment = getFutureDateString(daysUntilPayment)
        val previousPayment = getPastDateString(daysFromPayment)
        val currentDate = getCurrentDateString()

        // ДОБАВЬТЕ ЭТИ СТРОКИ - получаем объекты Date
        val nextPaymentDate = DateCalculator.getNextPaymentDate(1, 30)
        val previousPaymentDate = DateCalculator.getPreviousPaymentDate(1, 30)

        val priceTariff = 120L
        val formattedDateTime =
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        return ZONTData(
            daysUntilPayment,
            daysFromPayment,
            nextPayment,
            previousPayment,
            priceTariff,
            currentDate,
            formattedDateTime, // ← formattedDateTime ПЕРЕД Date объектами
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
    fun saveZONTData(data: ZONTData) {
        saveZONTData(data, "") // вызываем перегруженный метод с пустым статусом
    }

    @Suppress("UNUSED")  // ← ДОБАВЬ ЭТУ СТРОКУ
    fun saveZONTData(data: ZONTData, customStatus: String) {
        try {
            fileManager.formatPaymentDate(
                //Английское название для работы с файлами
                //fileManager.formatPaymentDate("zont")  // ← использует КЛЮЧИ
                "zont",
                data.daysUntilPayment,
                data.daysFromPayment,
                data.nextPayment,
                data.previousPayment,
                data.priceTariff,
                data.formattedDateTime,
                data.nextPaymentDate,
                data.previousPaymentDate,
                customStatus  // ← передаем кастомный статус
            )

            Toast.makeText(context, "Данные zont сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения zont: ${ex.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}