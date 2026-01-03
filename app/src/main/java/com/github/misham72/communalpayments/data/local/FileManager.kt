package com.github.misham72.communalpayments.data.local

import android.content.Context
import android.util.Log
import com.github.misham72.communalpayments.R
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileManager(private val context: Context) {


    fun savePeriodicPayment(
        readyHeader: String,            // Пример: если занесли в историю заголовком будет строка из "🟩🟩🟩". Если нет — заголовок будет пустым
        readyService: String,           // Пример: "Услуга: - Интернет."
        readySeparator1: String,         // Пример: "-----------------------------------------------------------"
        readyDateTime: String,          // Пример: "(27.12.2024 14:30)"
        readyStatus: String,            // Пример: "Статус: 🔴 ОПЛАЧЕНО"
        readySeparator2: String,         // Пример: "-----------------------------------------------------------"
        readyPreviousPayment: String,   // Пример: "Предыдущая оплата: - 1500"
        readyNextPayment: String,       // Пример: "Следующая оплата: - 1800"
        readyDaysAgo: String,           // Пример: "Оплата была: - 5 дней назад."
        readyDaysLeft: String,          // Пример: "След. оплата через: - 25 дней."
        readyTariff: String,        // Пример: "Стоимость тарифа: - 5.00 руб."
        fileName: String                // Имя файла для сохранения
    ) {
        val format = """
        |$readyHeader
        |$readyService
        |$readySeparator1
        |$readyDateTime
        |$readyStatus
        |$readySeparator2
        |$readyPreviousPayment
        |$readyNextPayment
        |$readyDaysAgo
        |$readyDaysLeft
        |$readyTariff
        """.trimMargin()

        saveToFile(fileName, format, true)
    }

    fun getFileName(serviceType: String): String {
        // FileManager работает с файлами по ключам
        return "${serviceType}_calculations.txt"
    }

    fun editFile(serviceType: String, content: String): Boolean {
        val fileName = getFileName(serviceType)
        return try {
            saveToFile(fileName, content, false)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun saveMeterPayment(   // // 3. FileManager внутри себя делает:- Собирает все строки в один большой текст

        readyHeader: String,            // Пример: если занесли в историю заголовком будет строка из "🟩🟩🟩". Если нет — заголовок будет пустым
        readyService: String,           // Пример: "Услуга   -   Свет"
        readySeparator1: String,    // Пример: "----------------------------------------------------------"
        readyDateTime: String,          // Пример: "(27.12.2024 14:30)"
        readyStatus: String,            // Пример: "Статус: 🔴 ОПЛАЧЕНО"
        readySeparator2: String,    // Пример: "----------------------------------------------------------"
        readyCurrentReading: String,    // Пример: "Текущие показания: - 100.50 кВт/ч"
        readyPreviousReading: String,   // Пример: "Пред. показания: - 80.00 кВт/ч"
        readyTariff: String,
        readyConsumption: String,       // Пример: "Расход: - 20.50 кВт/ч"
        readyPaymentSum: String,        // Пример: "Сумма оплаты: - 102.50 руб."
        fileName: String                // Имя файла для сохранения

    ) {

        val format = """
        |$readyHeader
        |$readyService
        |$readySeparator1
        |$readyDateTime
        |$readyStatus
        |$readySeparator2
        |$readyCurrentReading
        |$readyPreviousReading
        |$readyTariff
        |$readyConsumption
        |$readyPaymentSum
        
        """.trimMargin()


        saveToFile(fileName, format, true)
    }

    fun loadFromFile(serviceType: String): String {  // Этот метод используется для отображения истории в ваших экранах. Например, в ElectricityScreen может быть кнопка "Показать историю", которая вызывает:
        val tag = "FileManager" // ← Единый тег для всех логов FileManager

        val fileName = getFileName(serviceType)
        val stringBuilder = StringBuilder()

        try {
            BufferedReader(
                InputStreamReader(
                    context.openFileInput(fileName), StandardCharsets.UTF_8
                )
            ).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n")
                }
            }
        } catch (_: FileNotFoundException) {
            Log.w(tag, context.getString(R.string.log_file_not_found, fileName)) // ← Заменили println на Log.w (предупреждение)
            return context.getString(R.string.empty_history_message)
        } catch (e: IOException) {
            Log.e(tag, context.getString(R.string.log_file_read_error, fileName), e) // ← Заменили println на Log.e (ошибка с исключением)
            return context.getString(R.string.error_read_file_message, e.message)
        }

        return stringBuilder.toString()
    }

    private fun saveToFile(fileName: String, text: String, append: Boolean) {
        try {
            // Используем MODE_APPEND для добавления в конец файла
            val mode = if (append) Context.MODE_APPEND else Context.MODE_PRIVATE


            context.openFileOutput(fileName, mode).use { outputStream ->
                // Если append = true, просто добавляем новые данные в конец файла
                // Если append = false, файл очищается и записывается заново
                outputStream.write(text.toByteArray(StandardCharsets.UTF_8))
                outputStream.write("\n".toByteArray())  // Добавляем перенос строки
            }
        } catch (e: Exception) {
            // Просто заменяем println на Log.e (и добавляем e для stack trace)
            Log.e("FileManager", context.getString(R.string.log_error_saving_to_file, fileName, e.message), e)
        }
    }


    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat(context.getString(R.string.format_date_time_file), Locale.getDefault())
        return sdf.format(Date())
    }

    val formattedDateTime: String = getCurrentDateTime()

}