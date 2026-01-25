package com.github.misham72.communalpayments.domain.userclasses

import android.content.Context
import android.util.Log
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator

class Taxes(private val context: Context) {
    private val fileManager = FileManager(context)

    data class TaxesData(
        val isHistory: Boolean, val formattedDateTime: String, val customStatus: String, val previousPayment: String, val nextPayment: String, val daysFromPayment: Long, val daysUntilPayment: Long, val priceTariff: Long
    )

    fun collectTaxesData(): TaxesData {
        return TaxesData(
            isHistory = true,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = context.getString(R.string.status_paid),
            previousPayment = PeriodCalculator.getPreviousPaymentString(3, 29),
            nextPayment = PeriodCalculator.getNextPaymentString(3, 29),
            daysFromPayment = PeriodCalculator.calculateDaysFromPreviousPayment(3, 29),
            daysUntilPayment = PeriodCalculator.calculateDaysToNextPayment(3, 29),
            priceTariff = 13415L,
        )
    }

    fun saveTaxesData(data: TaxesData) {
        val tag = context.getString(R.string.service_key_taxes) // ← Добавьте тег!
        try {
            // 1. ПОДГОТОВКА ВСЕХ ГОТОВЫХ СТРОК:

            val readyHeader = if (data.isHistory) "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩" else ""   //  Если запись историческая (data.isHistory == true), то заголовком будет строка из "🟩🟩🟩". Если нет — заголовок будет пустым
            val serviceName = context.getString(R.string.service_display_name_taxes)   //  Текст на вкладке для пользователя - налоги.
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

// 2. ПРАВИЛЬНЫЙ ВЫЗОВ ФУНКЦИИ formatPaymentDate:
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
        } catch (e: Exception) {
            Log.e(tag, "❌ " + context.getString(R.string.error_saving), e)
        }
    }
}