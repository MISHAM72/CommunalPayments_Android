package com.github.misham72.communalpayments.logic

import android.content.Context
import android.util.Log
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.logic.calculators.DateCalculator

class Tinkoff(private val context: Context) {


    private val fileManager = FileManager(context)

    data class TinkoffData(
        val isHistory: Boolean, val formattedDateTime: String, val customStatus: String, val previousPayment: String, val nextPayment: String, val daysFromPayment: Long, val daysUntilPayment: Long, val priceTariff: Long
    )

    fun collectTinkoffData(): TinkoffData {
        return TinkoffData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = context.getString(R.string.status_paid),
            previousPayment = DateCalculator.getPreviousPaymentString(1, 23),
            nextPayment = DateCalculator.getNextPaymentString(1, 23),
            daysFromPayment = DateCalculator.calculateDaysFromPreviousPayment(1, 23),
            daysUntilPayment = DateCalculator.calculateDaysToNextPayment(1, 23),
            priceTariff = 402L,
        )
    }

    fun saveTinkoffData(data: TinkoffData) {

        val tag = context.getString(R.string.service_key_tinkoff) // ← Добавьте тег!
        try {
            // 1. ПОДГОТОВКА ВСЕХ ГОТОВЫХ СТРОК:

            val readyHeader = if (data.isHistory) "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩" else ""   //  Если запись историческая (data.isHistory == true), то заголовком будет строка из "🟩🟩🟩". Если нет — заголовок будет пустым
            val serviceName = context.getString(R.string.service_display_name_tinkoff)   //  Текст на вкладке для пользователя - Интернет.
            val readyService = context.getString(R.string.custom_ready_service, serviceName)  // Это шаблон "Услуга - %s", а serviceName - это значение "Свет", которое встанет на место %s.
            val readySeparator1 = "-----------------------------------------------------------"  // Разделитель.
            val readyDateTime = "(${data.formattedDateTime})"
            val readyStatus = if (data.customStatus.isNotEmpty()) context.getString(R.string.custom_status_paid, data.customStatus) else ""   // Статус для пользователя (если есть)
            val readySeparator2 = "-----------------------------------------------------------"  // Разделитель.
            val readyPreviousPayment = context.getString(R.string.previous_payment, data.previousPayment)
            val readyNextPayment = context.getString(R.string.next_payment, data.nextPayment)
            val readyDaysAgo = context.getString(R.string.days_from_payment, data.daysFromPayment)
            val readyDaysLeft = context.getString(R.string.days_until_payment, data.daysUntilPayment)
            val readyTariff = context.getString(R.string.tariff, data.priceTariff)
            val fileName = fileManager.getFileName(tag)

            fileManager.savePeriodicPayment(
                readyHeader = readyHeader,
                readyService = readyService,
                readySeparator1 = readySeparator1,
                readyDateTime = readyDateTime,
                readyStatus = readyStatus,
                readySeparator2 = readySeparator2,
                readyPreviousPayment = readyPreviousPayment,
                readyNextPayment = readyNextPayment,
                readyDaysAgo = readyDaysAgo,
                readyDaysLeft = readyDaysLeft,
                readyTariff = readyTariff,
                fileName = fileName
            )

            // ДОБАВЬТЕ логирование успеха:
            Log.i(tag, "✅ " + context.getString(R.string.data_saved))

        } catch (e: Exception) {
            Log.e(tag, "❌ " + context.getString(R.string.error_saving), e)
        }
    }
}
