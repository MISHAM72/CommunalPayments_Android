package com.github.misham72.communalpayments.data.repository.export

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.repository.PdfHistoryRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfHistoryRepositoryImpl(
    private val fileManager: FileManager,
    private val accountPrefs: AccountPreferences,
    private val cacheDir: File,
    private val packageName: String,
    private val statusCalculated: String,
    private val currentReadingPdf: String,
    private val previousReadingPdf: String,
    private val consumptionPdf: String,
    private val toBePaid: String,
    private val tariff: String,
    private val periodPdf: String,
    private val nextPaymentPdf: String,
    private val pdfTitleHistory: String,
    private val formed: String,

    private val personalAccountLabel: String,
    private val pdfTableDate: String,
    private val pdfTablePrevious: String,
    private val pdfTableCurrent: String,
    private val amount: String,
    private val pdfTableStatus: String,
    private val pdfTablePeriod: String,
    private val pdfAllHistoryTitle: String,
    private val pdfGenerated: String,
    private val sendPdf: String,
    private val dateFormatPattern: String,
    private val serviceDisplayNames: Map<String, String>,
    private val historyHeader: String      // "🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩🟩"

) : PdfHistoryRepository {
    override suspend fun exportSingleHistoryPdf(context: Context, serviceKey: String) {   // exportAndShare — основная публичная suspend-функция для экспорта и отправки PDF.
        withContext(Dispatchers.IO) {   //  переключает выполнение на фоновый поток ввода-вывода, чтобы не замораживать интерфейс.
            val historyText = fileManager.readHistory(serviceKey)// ← здесь строка читается из файла
            if (historyText.isBlank()) return@withContext//проверяет, что история не пустая
            val records = parseHistoryUniversal(historyText) // ← сюда передаётся historyText
            val isMeter = isMeterService(serviceKey) // Определение типа услуги – проверяет первую запись: если у неё isMeter == true, значит услуга счётчиковая (электричество, вода и т.п.), иначе – периодическая
            val customServiceName = accountPrefs.getCustomName(serviceKey).ifBlank {   // Получение названия услуги и номера счёта
                getServiceName(serviceKey)
            }
            val accountNumber = accountPrefs.getAccount(serviceKey)
            val pdfFile = generatePdf(records, customServiceName, accountNumber, isMeter)   // Возвращается готовый файл PDF во временной папке.

            withContext(Dispatchers.Main) {   // Возврат на главный поток – withContext(Dispatchers.Main) и вызов sharePdf(context, pdfFile), который отправляет файл через системное меню «Поделиться».
                sharePdf(context, pdfFile)
            }
        }
    }

    private data class UniversalRecord(
//Формируется список объектов UniversalRecord, это проект (шаблон) объекта, а не сам объект. Он существует в коде, но не содержит данных.
        val date: String,
        val status: String,
        val amount: String,
        val tariff: String,
        val currentReading: String = "",
        val previousReading: String = "",
        val consumption: String = "",
        val nextPayment: String = "",
        val periodMonths: String = "",
    )

    private fun isMeterService(serviceKey: String): Boolean {
        return serviceKey == ServiceKeys.ELECTRICITY ||
            serviceKey == ServiceKeys.WATER ||
            serviceKey == ServiceKeys.GAS
    }

    private fun parseHistoryUniversal(
        historyText: String
    ): List<UniversalRecord> {
        val blocks = historyText.split(historyHeader).filter { it.isNotBlank() }
        val records = mutableListOf<UniversalRecord>()

        for (block in blocks) {
            if (block.isBlank()) continue
            var date = ""
            var status = statusCalculated
            var amount = ""
            var tariffValue = ""
            var currentReading = ""
            var previousReading = ""
            var consumption = ""
            var nextPayment = ""
            var periodMonths = ""

            val lines = block.lines()
            for (line in lines) {

                val trimmed = line.trim()
                when {

                    @Suppress("HardcodedStringLiteral")
                    trimmed.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) -> date = trimmed

                    trimmed.startsWith(currentReadingPdf) -> currentReading = trimmed.substringAfter(":").trim()
                    trimmed.startsWith(previousReadingPdf) -> previousReading = trimmed.substringAfter(":").trim()
                    trimmed.startsWith(consumptionPdf) -> consumption = trimmed.substringAfter(":").trim()
                    trimmed.startsWith(toBePaid) -> amount = trimmed.substringAfter(":").trim()
                    trimmed.startsWith(tariff) -> tariffValue = trimmed.substringAfter(":").trim()
                    trimmed.startsWith(periodPdf) -> periodMonths = trimmed.substringAfter(":").trim()
                    trimmed.startsWith(nextPaymentPdf) -> nextPayment = trimmed.substringAfter(":").trim()
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
                    UniversalRecord(
                        // Сохранение записи – если в блоке была найдена дата (обязательное поле), создаётся объект UniversalRecord со всеми собранными полями и добавляется в итоговый список.
                        date = date,
                        status = status,
                        amount = amount,
                        tariff = tariffValue,
                        currentReading = currentReading,
                        previousReading = previousReading,
                        consumption = consumption,
                        nextPayment = nextPayment,
                        periodMonths = periodMonths,
                    )
                )
            }
        }
        return records   // после обработки всех блоков возвращается records.
    }

    private fun generatePdf(   //Назначение: сформировать PDF-документ во временном файле, красиво сверстав заголовок, информацию о дате/счёте и таблицу с историей.
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
        canvas.drawText(pdfTitleHistory.format(customServiceName), leftMargin, y, titlePaint)
        y += 35

        val labelPaint = Paint().apply { color = Color.BLACK; textSize = 14f; typeface = Typeface.DEFAULT_BOLD } // labelPaint – чёрный жирный шрифт без подчёркивания для подписей "Сформировано:" и "Лицевой счёт:".
        val label = formed
        val labelWidth = labelPaint.measureText(label)
        canvas.drawText(label, leftMargin, y, labelPaint)
        val dateStr = SimpleDateFormat(dateFormatPattern, Locale.getDefault()).format(Date())
        canvas.drawText(dateStr, leftMargin + labelWidth, y, infoPaint) // infoPaint с синим и подчёркиванием
        y += 25

        if (accountNumber.isNotBlank()) {
            val cleaned = accountNumber.trim()
            val label2 = personalAccountLabel // "Л/С:" без пробела
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
            canvas.drawText(pdfTableDate, xDate, y, headerFont)
            canvas.drawText(pdfTablePrevious, xPrev, y, headerFont)
            canvas.drawText(pdfTableCurrent, xCurr, y, headerFont)
            canvas.drawText(consumptionPdf, xCons, y, headerFont)
            canvas.drawText(tariff, xTariff, y, headerFont)
            canvas.drawText(amount, xAmnt, y, headerFont)
            canvas.drawText(pdfTableStatus, xStat, y, headerFont)
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
            canvas.drawText(pdfTableDate, xDate, y, headerFont)
            canvas.drawText(pdfTablePeriod, xPer, y, headerFont)
            canvas.drawText(nextPaymentPdf, xDay, y, headerFont)
            canvas.drawText(tariff, xTariff, y, headerFont)
            canvas.drawText(pdfTableStatus, xStat, y, headerFont)
            y += 25

            for (r in records) {
                canvas.drawText(r.date, xDate, y, tableFont)
                canvas.drawText(r.periodMonths, xPer, y, tableFont)
                canvas.drawText(r.nextPayment, xDay, y, tableFont)
                canvas.drawText(r.tariff, xTariff, y, tableFont)
                canvas.drawText(r.status, xStat, y, tableFont)
                y += 22
            }
        }

        document.finishPage(page)   // Завершение страницы – document.finishPage(page).

        val file = File(cacheDir, "history_${System.currentTimeMillis()}.pdf")   // Сохранение в файл – создаётся временный файл с именем history_<timestamp>.pdf в cacheDir
        document.writeTo(java.io.FileOutputStream(file))
        document.close()
        return file   // Возврат файла – функция отдаёт готовый File.
    }

    private fun sharePdf(context: Context, file: File) {   // Назначение: отправить PDF-файл через системное меню «Поделиться» (например, в мессенджер, почту, облако).
        val uri = FileProvider.getUriForFile(context, "${packageName}.fileprovider", file)   // Получение URI – через FileProvider.getUriForFile для заданного файла.
        val intent = Intent(Intent.ACTION_SEND).apply {   // Создание Intent – Intent.ACTION_SEND с типом application/pdf. В EXTRA_STREAM кладётся URI файла.
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)   // Флаги – FLAG_GRANT_READ_URI_PERMISSION даёт временное право на чтение файла получающему приложению.
        }
        context.startActivity(Intent.createChooser(intent, sendPdf))   // Запуск – context.startActivity(Intent.createChooser(...)) отображает диалог выбора приложения для отправки.
    }


    private fun getServiceName(serviceKey: String): String {  // Назначение: преобразовать внутренний ключ услуги (константы из ServiceKeys) в человекочитаемое название на русском языке.
        return serviceDisplayNames[serviceKey] ?: serviceKey
    }
    // Внутри объекта PdfHistoryExporter

    override suspend fun exportAllHistoryPdf(context: Context) {
        withContext(Dispatchers.IO) {

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
                    getServiceName(key)
                }
                val accountNumber = accountPrefs.getAccount(key)
                accountMap[serviceName] = accountNumber
                val records = parseHistoryUniversal(historyText)
                for (record in records) {
                    allRecords.add(serviceName to record)
                }
            }

            if (allRecords.isEmpty()) return@withContext

            val pdfFile = generateAllHistoryPdf(allRecords, accountMap)

            withContext(Dispatchers.Main) {
                sharePdf(context, pdfFile)
            }
        }
    }

    private fun generateAllHistoryPdf(
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
        canvas.drawText(pdfAllHistoryTitle, leftMargin, y, titlePaint)
        y += 35
        val dateStr = SimpleDateFormat(dateFormatPattern, Locale.getDefault()).format(Date())
        canvas.drawText(
            pdfGenerated.format(dateStr),
            leftMargin, y, infoFont
        )
        y += 30

        val grouped = records.groupBy { it.first }

        val serviceOrder = listOf(
            serviceDisplayNames[ServiceKeys.ELECTRICITY] ?: "",
            serviceDisplayNames[ServiceKeys.WATER] ?: "",
            serviceDisplayNames[ServiceKeys.GAS] ?: "",
            serviceDisplayNames[ServiceKeys.GARBAGE] ?: "",
            serviceDisplayNames[ServiceKeys.MTS] ?: "",
            serviceDisplayNames[ServiceKeys.TINKOFF] ?: "",
            serviceDisplayNames[ServiceKeys.TAXES] ?: "",
            serviceDisplayNames[ServiceKeys.TROYKA] ?: "",
            serviceDisplayNames[ServiceKeys.OSAGO] ?: "",
            serviceDisplayNames[ServiceKeys.HOSTEL] ?: ""
        )
        val orderMap = serviceOrder.withIndex().associate { it.value to it.index }
        val sortedGroups = grouped.entries.sortedBy { orderMap[it.key] ?: Int.MAX_VALUE }

        // Колонки таблицы (подобраны под портретную ширину)
        val xDate = 40f
        val xAmount = 200f
        val xStatus = 370f

        // Вспомогательная функция для рисования заголовков колонок
        fun drawColumnHeaders(canvas: Canvas, yPos: Float) {
            canvas.drawText(pdfTableDate, xDate, yPos, headerFont)
            canvas.drawText(amount, xAmount, yPos, headerFont)
            // canvas.drawText(statusLabel, xStatus, yPos, headerFont)
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
                val accountLabel = personalAccountLabel // "Л/С:" (без пробела)
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
            val dateFormat = SimpleDateFormat(dateFormatPattern, Locale.getDefault())
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

        val file = File(cacheDir, "history_all_${System.currentTimeMillis()}.pdf")
        document.writeTo(java.io.FileOutputStream(file))
        document.close()
        return file
    }
}
