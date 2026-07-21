package com.github.misham72.communalpayments.presentation.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfHistoryExporter {

    suspend fun exportAndShare(context: Context, serviceKey: String) {   // exportAndShare — основная публичная suspend-функция для экспорта и отправки PDF.
        withContext(Dispatchers.IO) {   //  переключает выполнение на фоновый поток ввода-вывода, чтобы не замораживать интерфейс.
            val fileManager = FileManager(context.applicationContext)  //Чтение истории – создает FileManager и AccountPreferences (им нужен applicationContext, чтобы избежать утечек).
            val accountPrefs = AccountPreferences(context.applicationContext)

            val historyText = fileManager.readHistory(serviceKey)
            if (historyText.isBlank()) return@withContext

            val records = parseHistoryUniversal(context, historyText)   // Парсинг текста – вызывает parseHistoryUniversal(historyText), получает список структурированных записей UniversalRecord.
            val isMeter = records.firstOrNull()?.isMeter ?: false   // Определение типа услуги – проверяет первую запись: если у неё isMeter == true, значит услуга счётчиковая (электричество, вода и т.п.), иначе – периодическая

            val customServiceName = accountPrefs.getCustomName(serviceKey).ifBlank {   // Получение названия услуги и номера счёта
                getServiceName(context, serviceKey)
            }
            val accountNumber = accountPrefs.getAccount(serviceKey)

            val pdfFile = generatePdf(context, records, customServiceName, accountNumber, isMeter)   // Возвращается готовый файл PDF во временной папке.

            withContext(Dispatchers.Main) {   // Возврат на главный поток – withContext(Dispatchers.Main) и вызов sharePdf(context, pdfFile), который отправляет файл через системное меню «Поделиться».
                sharePdf(context, pdfFile)
            }
        }
    }

    private data class UniversalRecord(
        val date: String,
        val status: String,
        val amount: String,
        val tariff: String,
        val currentReading: String = "",
        val previousReading: String = "",
        val consumption: String = "",
        val paymentDay: String = "",
        val periodMonths: String = "",
        val isMeter: Boolean
    )

    private fun parseHistoryUniversal(
        context: Context,
        text: String
    ): List<UniversalRecord> {   // преобразовать сырой текст истории (тот самый, что записан в файле для каждой услуги) в список объектов UniversalRecord, удобных для отображения в таблице PDF.
        val records = mutableListOf<UniversalRecord>()

        @Suppress("HardcodedStringLiteral")
        val blocks = text.split(Regex("(?<=\\n|^)(?=🟩{14})"))   // Разбиение на блоки – исходный текст разделяется регулярным выражением, которое ищет строки, начинающиеся с последовательности 14 зелёных квадратов (🟩{14})

        for (block in blocks) {
            if (block.isBlank()) continue

            var date = ""
            var status = context.getString(R.string.status_calculated)
            var amount = ""
            var tariff = ""
            var currentReading = ""
            var previousReading = ""
            var consumption = ""
            var paymentDay = ""
            var periodMonths = ""
            var isMeter = false

            val lines = block.lines()
            for (line in lines) {
                val trimmed = line.trim()
                when {   // Построчный разбор – блок делится на строки. Для каждой строки с помощью when проверяется, с чего она начинается:
                    @Suppress("HardcodedStringLiteral")
                    trimmed.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) -> date = trimmed

                    trimmed.startsWith(context.getString(R.string.current_reading_pdf)) -> {
                        currentReading = trimmed.substringAfter(":").replace("-", "").trim()
                        isMeter = true
                    }

                    trimmed.startsWith(context.getString(R.string.previous_reading_pdf)) -> {
                        previousReading = trimmed.substringAfter(":").replace("-", "").trim()
                        isMeter = true
                    }

                    trimmed.startsWith(context.getString(R.string.consumption)) -> consumption = trimmed.substringAfter(":").replace("-", "").trim()
                    trimmed.startsWith(context.getString(R.string.to_be_paid)) -> amount = trimmed.substringAfter(":").replace("-", "").replace(context.getString(R.string.ru), "").trim()
                    trimmed.startsWith(context.getString(R.string.tariff)) -> tariff = trimmed.substringAfter(":").replace(context.getString(R.string.ru), "").trim()
                    trimmed.startsWith(context.getString(R.string.day_of_payment_format).substringBefore("%")) -> paymentDay = trimmed.substringAfter(":").trim()
                    @Suppress("HardcodedStringLiteral")
                    trimmed.startsWith("Период:") -> periodMonths = trimmed.substringAfter(":").replace("%d", "").replace(context.getString(R.string.mon), "").trim()
                    // Статусы с эмодзи (эмодзи остаются)
                    trimmed.startsWith("\uD83D\uDD34") -> status = trimmed   // 🔴
                    trimmed.startsWith("\u23F3") -> status = trimmed          // ⏳
                    trimmed.startsWith("\u2705") -> status = trimmed          // ✅
                    trimmed.startsWith("\uD83D\uDD0D") -> status = trimmed    // 🔍
                    trimmed.startsWith("\uD83D\uDEAB") -> status = trimmed    // 🚫
                    trimmed.startsWith("\uD83E\uDD13") -> status = trimmed    // 🤓
                }
            }

            if (date.isNotBlank()) {
                records.add(
                    UniversalRecord(  // Сохранение записи – если в блоке была найдена дата (обязательное поле), создаётся объект UniversalRecord со всеми собранными полями и добавляется в итоговый список.
                        date = date,
                        status = status,
                        amount = amount,
                        tariff = tariff,
                        currentReading = currentReading,
                        previousReading = previousReading,
                        consumption = consumption,
                        paymentDay = paymentDay,
                        periodMonths = periodMonths,
                        isMeter = isMeter
                    )
                )
            }
        }
        return records   // после обработки всех блоков возвращается records.
    }

    private fun generatePdf(   //Назначение: сформировать PDF-документ во временном файле, красиво сверстав заголовок, информацию о дате/счёте и таблицу с историей.
        context: Context,
        records: List<UniversalRecord>,
        customServiceName: String,
        accountNumber: String,
        isMeter: Boolean
    ): File {
        val document = PdfDocument()   // Создание PDF-документа и страницы – задаётся альбомный лист A4 (842x595 точек). Canvas позволяет рисовать текст и примитивы.
        val pageWidth = 842   // альбомная A4
        val pageHeight = 595
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Единый шрифт для таблицы
        val tableFont = Paint().apply {   // tableFont – обычный чёрный шрифт для данных таблицы (размер 12, без жирности).
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.DEFAULT
        }

        val titlePaint = Paint().apply {   // titlePaint – крупный жирный шрифт для заголовка ("История: ...").
            color = Color.BLACK
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
        }
// 🆕 Яркий шрифт для даты и лицевого счёта
        val infoPaint = Paint().apply {   // infoPaint – синий жирный шрифт с подчёркиванием для значений даты формирования и номера счёта.
            color = Color.BLUE
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            isUnderlineText = true
        }
        var y = 40f
        val leftMargin = 40f

        // Заголовок
        canvas.drawText(context.getString(R.string.pdf_title_history, customServiceName), leftMargin, y, titlePaint)
        y += 35

        val labelPaint = Paint().apply { color = Color.BLACK; textSize = 14f; typeface = Typeface.DEFAULT_BOLD } // labelPaint – чёрный жирный шрифт без подчёркивания для подписей "Сформировано:" и "Лицевой счёт:".
        val label = context.getString(R.string.formed)
        val labelWidth = labelPaint.measureText(label)
        canvas.drawText(label, leftMargin, y, labelPaint)
        val dateStr = SimpleDateFormat(context.getString(R.string.yyyy_mm_dd_hh_mm_ss), Locale.getDefault()).format(Date())
        canvas.drawText(dateStr, leftMargin + labelWidth, y, infoPaint) // infoPaint с синим и подчёркиванием
        y += 25

        if (accountNumber.isNotBlank()) {
            val cleaned = accountNumber.trim()
            val label2 = context.getString(R.string.personal_account_label) // "Л/С:" без пробела
            val labelWidth = labelPaint.measureText(label2)
            // Отступ между меткой и номером
            val gap = 10f
            canvas.drawText(label2, leftMargin, y, labelPaint)
            canvas.drawText(cleaned, leftMargin + labelWidth + gap, y, infoPaint)
            y += 25
        }

        val headerFont = Paint().apply {   // headerFont – чёрный жирный шрифт с подчёркиванием для заголовков столбцов таблицы.
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD          // жирный шрифт для заголовков
            isUnderlineText = true                   // включает подчёркивание
        }
        // Координаты колонок
        if (isMeter) {   // Отрисовка таблицы – ветвление по isMeter. Для счётчиковых услуг (электричество, вода и т.д.):
            val xDate = 40f
            val xPrev = 180f
            val xCurr = 290f
            val xCons = 400f
            val xTariff = 500f
            val xAmnt = 565f
            val xStat = 650f

            // Заголовки для счётчиков
            canvas.drawText(context.getString(R.string.pdf_table_date), xDate, y, headerFont)
            canvas.drawText(context.getString(R.string.pdf_table_previous), xPrev, y, headerFont)
            canvas.drawText(context.getString(R.string.pdf_table_current), xCurr, y, headerFont)
            canvas.drawText(context.getString(R.string.pdf_table_consumption), xCons, y, headerFont)
            canvas.drawText(context.getString(R.string.tariff), xTariff, y, headerFont)
            canvas.drawText(context.getString(R.string.amount), xAmnt, y, headerFont)
            canvas.drawText(context.getString(R.string.pdf_table_status), xStat, y, headerFont)
            y += 25   // Если убрать, то данные смещаются на заголовки
            for (r in records) {
                canvas.drawText(r.date, xDate, y, tableFont)
                canvas.drawText(r.previousReading, xPrev, y, tableFont)
                canvas.drawText(r.currentReading, xCurr, y, tableFont)
                canvas.drawText(r.consumption, xCons, y, tableFont)
                canvas.drawText(r.tariff, xTariff, y, tableFont)
                canvas.drawText(r.amount, xAmnt, y, tableFont)
                canvas.drawText(r.status, xStat, y, tableFont)
                y += 22
            }
        } else {  // Для периодических услуг (общежитие и пр.):
            val xDate = 40f
            val xPer = 190f
            val xDay = 305f
            val xTariff = 425f
            val xStat = 525f

            // Заголовки для периодических
            canvas.drawText(context.getString(R.string.pdf_table_date), xDate, y, headerFont)
            canvas.drawText(context.getString(R.string.pdf_table_period), xPer, y, headerFont)
            canvas.drawText(context.getString(R.string.pdf_table_payment_day), xDay, y, headerFont)
            canvas.drawText(context.getString(R.string.tariff), xTariff, y, headerFont)
            canvas.drawText(context.getString(R.string.pdf_table_status), xStat, y, headerFont)
            y += 25

            for (r in records) {
                canvas.drawText(r.date, xDate, y, tableFont)
                canvas.drawText(r.periodMonths, xPer, y, tableFont)
                canvas.drawText(r.paymentDay, xDay, y, tableFont)
                canvas.drawText(r.tariff, xTariff, y, tableFont)
                canvas.drawText(r.status, xStat, y, tableFont)
                y += 22
            }
        }

        document.finishPage(page)   // Завершение страницы – document.finishPage(page).

        val file = File(context.cacheDir, "history_${System.currentTimeMillis()}.pdf")   // Сохранение в файл – создаётся временный файл с именем history_<timestamp>.pdf в cacheDir
        document.writeTo(java.io.FileOutputStream(file))
        document.close()
        return file   // Возврат файла – функция отдаёт готовый File.
    }

    private fun sharePdf(context: Context, file: File) {   // Назначение: отправить PDF-файл через системное меню «Поделиться» (например, в мессенджер, почту, облако).
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)   // Получение URI – через FileProvider.getUriForFile для заданного файла.
        val intent = Intent(Intent.ACTION_SEND).apply {   // Создание Intent – Intent.ACTION_SEND с типом application/pdf. В EXTRA_STREAM кладётся URI файла.
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)   // Флаги – FLAG_GRANT_READ_URI_PERMISSION даёт временное право на чтение файла получающему приложению.
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.send_pdf)))   // Запуск – context.startActivity(Intent.createChooser(...)) отображает диалог выбора приложения для отправки.
    }


    private fun getServiceName(context: Context, serviceKey: String): String {  // Назначение: преобразовать внутренний ключ услуги (константы из ServiceKeys) в человекочитаемое название на русском языке.

        val resId = when (serviceKey) {
            ServiceKeys.ELECTRICITY -> R.string.service_display_name_electricity
            ServiceKeys.GAS -> R.string.service_display_name_gas
            ServiceKeys.WATER -> R.string.service_display_name_water
            ServiceKeys.GARBAGE -> R.string.service_display_name_garbage
            ServiceKeys.ZONT -> R.string.service_display_name_zont
            ServiceKeys.INTERNET -> R.string.service_display_name_internet
            ServiceKeys.MTS -> R.string.service_display_name_mts
            ServiceKeys.TINKOFF -> R.string.service_display_name_tinkoff
            ServiceKeys.TAXES -> R.string.service_display_name_taxes
            ServiceKeys.TROYKA -> R.string.service_display_name_troyka
            ServiceKeys.OSAGO -> R.string.service_display_name_osago
            ServiceKeys.HOSTEL -> R.string.service_display_name_hostel   // если ещё нет – добавьте в XML
            else -> return serviceKey   // fallback
        }
        return context.getString(resId)
    }
    // Внутри объекта PdfHistoryExporter

    suspend fun exportAllHistoryPdf(context: Context) {
        withContext(Dispatchers.IO) {
            val fileManager = FileManager(context.applicationContext)
            val accountPrefs = AccountPreferences(context.applicationContext)

            val allKeys = listOf(
                ServiceKeys.ELECTRICITY, ServiceKeys.GAS, ServiceKeys.WATER,
                ServiceKeys.GARBAGE, ServiceKeys.ZONT, ServiceKeys.INTERNET,
                ServiceKeys.MTS, ServiceKeys.TINKOFF, ServiceKeys.TAXES,
                ServiceKeys.TROYKA, ServiceKeys.OSAGO, ServiceKeys.HOSTEL
            )

            val allRecords = mutableListOf<Pair<String, UniversalRecord>>()
            val accountMap = mutableMapOf<String, String>()

            for (key in allKeys) {
                val historyText = fileManager.readHistory(key)
                if (historyText.isBlank()) continue

                val serviceName = accountPrefs.getCustomName(key).ifBlank {
                    getServiceName(context, key)
                }
                val accountNumber = accountPrefs.getAccount(key)
                accountMap[serviceName] = accountNumber

                val records = parseHistoryUniversal(context, historyText)
                for (record in records) {
                    allRecords.add(serviceName to record)
                }
            }

            if (allRecords.isEmpty()) return@withContext

            val pdfFile = generateAllHistoryPdf(context, allRecords, accountMap)

            withContext(Dispatchers.Main) {
                sharePdf(context, pdfFile)
            }
        }
    }

    private fun generateAllHistoryPdf(
        context: Context,
        records: List<Pair<String, UniversalRecord>>,
        accountMap: Map<String, String>
    ): File {
        val document = PdfDocument()
        val pageWidth = 595   // книжная A4
        val pageHeight = 842

        // Шрифты
        val tableFont = Paint().apply {
            color = Color.BLACK; textSize = 11f; typeface = Typeface.DEFAULT
        }
        val titlePaint = Paint().apply {
            color = Color.BLACK; textSize = 20f; typeface = Typeface.DEFAULT_BOLD
        }
        val headerFont = Paint().apply {
            color = Color.BLACK; textSize = 11f; typeface = Typeface.DEFAULT_BOLD
            isUnderlineText = true
        }
        val accountPaint = Paint().apply {
            color = Color.BLUE; textSize = 13f; typeface = Typeface.DEFAULT_BOLD
            isUnderlineText = true
        }
        val serviceHeaderFont = Paint().apply {
            color = Color.BLACK; textSize = 13f; typeface = Typeface.DEFAULT_BOLD
            isUnderlineText = true
        }
        val infoFont = Paint().apply {
            color = Color.BLACK; textSize = 11f; typeface = Typeface.DEFAULT
        }

        fun createNewPage(): Pair<Canvas, PdfDocument.Page> {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            return page.canvas to page
        }

        var currentPage = createNewPage()
        var canvas = currentPage.first
        var y = 40f
        val leftMargin = 40f
        val bottomLimit = pageHeight - 30f

        // Главный заголовок (только на первой странице)
        canvas.drawText(context.getString(R.string.pdf_all_history_title), leftMargin, y, titlePaint)
        y += 35
        val dateStr = SimpleDateFormat(context.getString(R.string.yyyy_mm_dd_hh_mm_ss), Locale.getDefault()).format(Date())
        canvas.drawText(
            context.getString(R.string.pdf_generated, dateStr),
            leftMargin, y, infoFont
        )
        y += 30

        val grouped = records.groupBy { it.first }

        val serviceOrder = listOf(
            context.getString(R.string.service_display_name_electricity),
            context.getString(R.string.service_display_name_water),
            context.getString(R.string.service_display_name_gas),
            context.getString(R.string.service_display_name_garbage),
            context.getString(R.string.service_display_name_mts),
            context.getString(R.string.service_display_name_tinkoff),
            context.getString(R.string.service_display_name_taxes),
            context.getString(R.string.service_display_name_troyka),
            context.getString(R.string.service_display_name_osago),
            context.getString(R.string.service_display_name_hostel),
        )
        val orderMap = serviceOrder.withIndex().associate { it.value to it.index }
        val sortedGroups = grouped.entries.sortedBy { orderMap[it.key] ?: Int.MAX_VALUE }

        // Колонки таблицы (подобраны под портретную ширину)
        val xDate = 40f
        val xAmount = 200f
        val xStatus = 370f

        // Вспомогательная функция для рисования заголовков колонок
        fun drawColumnHeaders(canvas: Canvas, yPos: Float) {
            canvas.drawText(context.getString(R.string.pdf_table_date), xDate, yPos, headerFont)
            canvas.drawText(context.getString(R.string.amount), xAmount, yPos, headerFont)
            canvas.drawText(context.getString(R.string.status_label), xStatus, yPos, headerFont)
        }

        for ((serviceName, serviceRecords) in sortedGroups) {
            // Проверяем, хватит ли места для подзаголовка + заголовков + хотя бы одной строки
            val neededHeight = 22f + 20f + 22f
            if (y + neededHeight > bottomLimit) {
                document.finishPage(currentPage.second)
                currentPage = createNewPage()
                canvas = currentPage.first
                y = 40f
            }

            // Подзаголовок услуги с лицевым счётом
            val account = accountMap[serviceName]?.takeIf { it.isNotBlank() }
            if (account != null) {
                canvas.drawText(serviceName, leftMargin, y, serviceHeaderFont)
                val serviceNameWidth = serviceHeaderFont.measureText(serviceName)
                val accountLabel = context.getString(R.string.personal_account_label) // "Л/С:" (без пробела)
                // Не добавляем пробел в метку
                val labelWidth = accountPaint.measureText(accountLabel)
                // Отступ между текстами (например, 10 пикселей)
                val gap = 10f
                canvas.drawText(accountLabel, leftMargin + serviceNameWidth + gap, y, accountPaint)
                canvas.drawText(account, leftMargin + serviceNameWidth + gap + labelWidth + 2f, y, accountPaint)
            } else {
                canvas.drawText(serviceName, leftMargin, y, serviceHeaderFont)
            }
            y += 22
            // Заголовки колонок
            drawColumnHeaders(canvas, y)
            y += 20

            // Сортировка записей внутри услуги
            val dateFormat = SimpleDateFormat(context.getString(R.string.yyyy_mm_dd_hh_mm_ss), Locale.getDefault())
            val sortedRecords = serviceRecords.sortedByDescending { record ->
                try {
                    dateFormat.parse(record.second.date)?.time ?: 0L
                } catch (_: Exception) {
                    0L
                }
            }

            for ((_, record) in sortedRecords) {
                if (y + 22f > bottomLimit) {
                    // Начинаем новую страницу
                    document.finishPage(currentPage.second)
                    currentPage = createNewPage()
                    canvas = currentPage.first
                    y = 40f
                    // Повторяем заголовки колонок на новой странице
                    drawColumnHeaders(canvas, y)
                    y += 20
                }
                canvas.drawText(record.date, xDate, y, tableFont)
                val money = record.amount.ifBlank { record.tariff }
                canvas.drawText(money, xAmount, y, tableFont)
                canvas.drawText(record.status, xStatus, y, tableFont)
                y += 22
            }

            y += 12  // отступ между услугами
        }

        document.finishPage(currentPage.second)

        val file = File(context.cacheDir, "history_all_${System.currentTimeMillis()}.pdf")
        document.writeTo(java.io.FileOutputStream(file))
        document.close()
        return file
    }
}
