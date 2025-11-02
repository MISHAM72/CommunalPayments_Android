package com.github.misham72.communalpayments.logic

import android.content.Context
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileManager(private val context: Context) {

    fun formatPaymentDate(
        isHistory: Boolean = true,
        serviceType: String,

        formattedDateTime: String,
        customStatus: String = "",

        previousPayment: String,
        nextPayment: String,

        daysFromPayment: Long,
        daysUntilPayment: Long,
        priceTariff: Long


    ) {
        val fileName = getFileName(serviceType)

        val header = if (isHistory) "|🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩" else ""


        val format = """
            |$header
            |Услуга:  -   $serviceType
            |-----------------------------------------------------------
            |($formattedDateTime)
            |-----------------------------------------------------------
            |${if (customStatus.isNotEmpty()) "Статус: $customStatus" else ""}
            |-----------------------------------------------------------
            |Предыдущая  оплата: - $previousPayment
            |Следующая  оплата:  - $nextPayment
            
            |Оплата была: - $daysFromPayment дней назад.
            |След. оплата через: - $daysUntilPayment дней.
           
            |    Стоимость тарифа: - $priceTariff руб.
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

    fun formatMeterReadingPaymentData(
        isHistory: Boolean = false,  // ← НОВЫЙ ПАРАМЕТР
        serviceType: String,
        formattedDateTime: String,
        customStatus: String = "",

        currentReading: Double,
        previousReading: Double,
        consumption: Double,

        tariff: Double,
        payment: Double,
        unit: String,


        ) {
        val fileName = getFileName(serviceType)

        val header = if (isHistory) "|🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩" else ""


        val format = """
            |$header
            |Услуга   -   $serviceType
            |----------------------------------------------------------
            |( $formattedDateTime)
            |----------------------------------------------------------
            |${if (customStatus.isNotEmpty()) "Статус: $customStatus" else ""}
            |----------------------------------------------------------
            |Текущие показания:  - ${"%.2f".format(currentReading)} $unit
            |Пред. показания:     - ${"%.2f".format(previousReading)} $unit
            ||Тариф:                - ${"%.2f".format(tariff)} руб.
            |Расход:                  - ${"%.2f".format(consumption)} $unit
            |Сумма оплаты:- ${"%.2f".format(payment)} руб.        
            """.trimMargin()

        saveToFile(fileName, format, true)
    }

    fun loadFromFile(serviceType: String): String {
        val fileName = getFileName(serviceType)
        val stringBuilder = StringBuilder()
        println("🔍 ЗАПРОС ФАЙЛА: $fileName") // ← добавить

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
            println("❌ ФАЙЛ НЕ НАЙДЕН: $fileName") // ← добавить
            return "История расчетов пуста"
        } catch (e: IOException) {
            println("❌ ОШИБКА ЧТЕНИЯ: ${e.message}") // ← добавить
            return "Ошибка чтения файла: ${e.message}"
        }

        return stringBuilder.toString()
    }

    private fun saveToFile(fileName: String, text: String, append: Boolean) {
        if (append) {
            // 1. Читаем ВЕСЬ существующий файл
            val oldContent = try {
                context.openFileInput(fileName).bufferedReader().use { it.readText() }
            } catch (_: FileNotFoundException) {
                ""
            }

            // 2. Записываем: СНАЧАЛА НОВЫЕ данные, ПОТОМ старые
            FileOutputStream(context.getFileStreamPath(fileName), false).use { fos ->
                val newContent = text + oldContent  // ← новые ПЕРВЫЕ!
                fos.write(newContent.toByteArray(StandardCharsets.UTF_8))
            }
        } else {
            // Простая перезапись
            FileOutputStream(context.getFileStreamPath(fileName), false).use { fos ->
                fos.write(text.toByteArray(StandardCharsets.UTF_8))
            }
        }
    }


    fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy _ HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    val formattedDateTime = getCurrentDateTime()
}