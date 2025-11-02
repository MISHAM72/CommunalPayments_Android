package com.github.misham72.communalpayments.logic

import android.content.Context
import android.widget.Toast

/**Ответственность: Бизнес-логика конкретно для ZONT. Что делает:
Знает специфику ZONT (тариф 402 рубля, день платежа 23 число)
Формирует структуру данных для ZONT
Определяет статусы оплаты ("🔴 ОПЛАЧЕНО"). Сохраняет данные ZONT через FileManager
Взаимодействует с пользователем (показывает Toast) */
class ZONT(private val context: Context?) {

    private val fileManager = FileManager(context!!)

    data class ZONTData(
        val isHistory: Boolean,
        val formattedDateTime: String,
        val customStatus: String,
        val previousPayment: String,
        val nextPayment: String,
        val daysFromPayment: Long,
        val daysUntilPayment: Long,
        val priceTariff: Long,

        )

    fun calculateZONTData(): ZONTData {
        return ZONTData(
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

    /**2. Сохранение данных.Передает все данные в FileManager для форматирования и записи в файл  */
    @Suppress("UNUSED")  // ← ДОБАВЬ ЭТУ СТРОКУ
    fun saveZONTData(data: ZONTData) {
        try {
            fileManager.formatPaymentDate(
                data.isHistory,
                "zont",

                data.formattedDateTime,
                data.customStatus,
                data.previousPayment,
                data.nextPayment,
                data.daysFromPayment,
                data.daysUntilPayment,
                data.priceTariff
            )
            //3. Уведомление об успехе
            Toast.makeText(context, "Данные zont сохранены!", Toast.LENGTH_SHORT).show()

        } catch (ex: Exception) {
            Toast.makeText(context, "Ошибка сохранения zont: ${ex.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}