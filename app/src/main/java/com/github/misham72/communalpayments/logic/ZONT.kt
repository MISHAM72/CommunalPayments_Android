package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ZONT(private val context: Context?) {

    private val fileManager = FileManager(context!!)

    data class ZONTData(
        val formattedDateTime: String,
        val previousPayment: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Long,

        )

    fun calculateZONTData(): ZONTData {
        val formattedDateTime =
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        val daysUntilPayment = DateCalculator.calculateDaysToNextPayment(1, 23)
        val daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(1, 23)

        val previousPayment = getPastDateString(daysFromPayment)  // ← "30.10.2023"
        val nextPayment = getFutureDateString(daysUntilPayment)   // ← "30.11.2023"
        val priceTariff = 402L

        return ZONTData(
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
    fun saveZONTData(data: ZONTData) {
        saveZONTData(data, "") // вызываем перегруженный метод с пустым статусом
    }

    @Suppress("UNUSED")  // ← ДОБАВЬ ЭТУ СТРОКУ
    fun saveZONTData(data: ZONTData, customStatus: String) {
        try {
            fileManager.formatPaymentDate(
                "zont",
                data.formattedDateTime,
                customStatus = customStatus,
                data.previousPayment,
                data.nextPayment,
                data.daysFromPayment,
                data.daysUntilPayment,
                data.priceTariff,
            )

            Toast.makeText(context, "Данные zont сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения zont: ${ex.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}