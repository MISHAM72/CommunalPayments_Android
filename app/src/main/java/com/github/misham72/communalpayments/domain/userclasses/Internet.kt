package com.github.misham72.communalpayments.domain.userclasses

import android.content.Context
import android.util.Log
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.calculators.PeriodCalculator

/**Класс Internet — это "мозг" для интернет-платежей, который:
✅ Принимает ввод пользователя
✅ Проверяет корректность данных
✅ Рассчитывает все необходимые даты
✅ Форматирует результат
✅ Сохраняет в файл**/
class Internet(private val context: Context) {
    private val fileManager = FileManager(context)

    data class InternetData(
        val isHistory: Boolean,
        val formattedDateTime: String,
        val customStatus: String,
        val previousPayment: String,
        val daysFromPayment: Long,
        val nextPayment: String,
        val daysUntilPayment: Long,
        val priceTariff: Double
    )

    fun collectInternetData(
        paymentDay: Int,
        periodMonths: Int,
        tariffValue: Double
    ): InternetData? {
        // 1. ВАЛИДАЦИЯ (проверка входных данных)// Возвращаем null при некорректных данных
        if (paymentDay !in 1..31 || periodMonths <= 0 || tariffValue <= 0.0) {
            return null
        }
        // 2. ИСПОЛЬЗОВАНИЕ ПЕРЕДАННЫХ ПАРАМЕТРОВ
        // 👇 2. РАСЧЕТ ВСЕХ НЕОБХОДИМЫХ ДАТ И СРОКОВ
        val previousPayment = PeriodCalculator.getPreviousPaymentString(periodMonths, paymentDay)
        val nextPayment = PeriodCalculator.getNextPaymentString(periodMonths, paymentDay)
        val daysFromPayment = PeriodCalculator.calculateDaysFromPreviousPayment(periodMonths, paymentDay)
        val daysUntilPayment = PeriodCalculator.calculateDaysToNextPayment(periodMonths, paymentDay)
        // 👆 РАСЧЕТ ЗАВЕРШЕН
        // 3. СОЗДАНИЕ ОБЪЕКТА С ДАННЫМИ
        return InternetData(
            isHistory = false,
            formattedDateTime = fileManager.getCurrentDateTime(),
            customStatus = context.getString(R.string.status_paid),
            previousPayment = previousPayment,
            nextPayment = nextPayment,
            daysFromPayment = daysFromPayment,
            daysUntilPayment = daysUntilPayment,
            priceTariff = tariffValue,
        )
    }

    fun saveInternetData(data: InternetData) {
        val tag = context.getString(R.string.service_key_internet) // ← Добавьте тег!
        try {
            // 1. ПОДГОТОВКА ВСЕХ ГОТОВЫХ СТРОК:
            // 👇 НАЧАЛО ФОРМАТИРОВАНИЯ РЕЗУЛЬТАТА
            val readyHeader = if (data.isHistory) "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩" else ""   //  Если запись историческая (data.isHistory == true), то заголовком будет строка из "🟩🟩🟩". Если нет — заголовок будет пустым
            val serviceName = context.getString(R.string.service_display_name_internet)   //  Текст на вкладке для пользователя - Интернет.
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
            // 👆 ЗАВЕРШЕНИЕ ФОРМАТИРОВАНИЯ
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