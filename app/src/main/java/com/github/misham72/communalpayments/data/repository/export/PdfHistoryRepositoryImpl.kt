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
import com.github.misham72.communalpayments.data.parser.HistoryParser
import com.github.misham72.communalpayments.domain.constants.ServiceKeys
import com.github.misham72.communalpayments.domain.model.HistoryRecord
import com.github.misham72.communalpayments.domain.repository.PdfHistoryRepository
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
    private val historyParser: HistoryParser,

    // Метки для отрисовки заголовков столбцов PDF
    private val consumptionPdf: String,
    private val tariff: String,
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
    private val serviceDisplayNames: Map<String, String>
) : PdfHistoryRepository {

    override suspend fun exportSingleHistoryPdf(context: Context, serviceKey: String) {
        withContext(Dispatchers.IO) {
            val historyText = fileManager.readHistory(serviceKey)
            if (historyText.isBlank()) return@withContext

            val records = historyParser.parse(historyText, serviceKey)
            val isMeter = isMeterService(serviceKey)
            val customServiceName = accountPrefs.getCustomName(serviceKey).ifBlank {
                getServiceName(serviceKey)
            }
            val accountNumber = accountPrefs.getAccount(serviceKey)
            val pdfFile = generatePdf(records, customServiceName, accountNumber, isMeter)

            withContext(Dispatchers.Main) {
                sharePdf(context, pdfFile)
            }
        }
    }

    private fun isMeterService(serviceKey: String): Boolean {
        return serviceKey == ServiceKeys.ELECTRICITY ||
            serviceKey == ServiceKeys.COLDWATER ||
            serviceKey == ServiceKeys.HOTWATER ||
            serviceKey == ServiceKeys.GAS
    }

    private fun generatePdf(
        records: List<HistoryRecord>,
        customServiceName: String,
        accountNumber: String,
        isMeter: Boolean
    ): File {
        val document = PdfDocument()
        val pageWidth = 842
        val pageHeight = 595
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val tableFont = Paint().apply {
            color = Color.BLACK; textSize = 12f; typeface = Typeface.DEFAULT
        }
        val titlePaint = Paint().apply {
            color = Color.BLACK; textSize = 22f; typeface = Typeface.DEFAULT_BOLD
        }
        val infoPaint = Paint().apply {
            color = Color.BLUE; textSize = 14f; typeface = Typeface.DEFAULT_BOLD
            isUnderlineText = true
        }
        var y = 40f
        val leftMargin = 40f

        canvas.drawText(pdfTitleHistory.format(customServiceName), leftMargin, y, titlePaint)
        y += 35

        val labelPaint = Paint().apply {
            color = Color.BLACK; textSize = 14f; typeface = Typeface.DEFAULT_BOLD
        }
        val label = formed
        val labelWidth = labelPaint.measureText(label)
        canvas.drawText(label, leftMargin, y, labelPaint)
        val dateStr = SimpleDateFormat(dateFormatPattern, Locale.getDefault()).format(Date())
        canvas.drawText(dateStr, leftMargin + labelWidth, y, infoPaint)
        y += 25

        if (accountNumber.isNotBlank()) {
            val cleaned = accountNumber.trim()
            val label2 = personalAccountLabel
            val labelWidth2 = labelPaint.measureText(label2)
            val gap = 10f
            canvas.drawText(label2, leftMargin, y, labelPaint)
            canvas.drawText(cleaned, leftMargin + labelWidth2 + gap, y, infoPaint)
            y += 25
        }

        val headerFont = Paint().apply {
            color = Color.BLACK; textSize = 12f; typeface = Typeface.DEFAULT_BOLD
            isUnderlineText = true
        }

        if (isMeter) {
            val xDate = 20f
            val xPrev = 120f
            val xCurr = 240f
            val xCons = 360f
            val xTariff = 480f
            val xAmnt = 570f
            val xStat = 670f

            canvas.drawText(pdfTableDate, xDate, y, headerFont)
            canvas.drawText(pdfTablePrevious, xPrev, y, headerFont)
            canvas.drawText(pdfTableCurrent, xCurr, y, headerFont)
            canvas.drawText(consumptionPdf, xCons, y, headerFont)
            canvas.drawText(tariff, xTariff, y, headerFont)
            canvas.drawText(amount, xAmnt, y, headerFont)
            canvas.drawText(pdfTableStatus, xStat, y, headerFont)
            y += 25

            for (r in records) {
                canvas.drawText(r.dateTime.substringBefore(" "), xDate, y, tableFont)
                canvas.drawText(r.previousReading, xPrev, y, tableFont)
                canvas.drawText(r.currentReading, xCurr, y, tableFont)
                canvas.drawText(r.consumption, xCons, y, tableFont)
                canvas.drawText(r.tariff, xTariff, y, tableFont)
                canvas.drawText(r.amount, xAmnt, y, tableFont)
                canvas.drawText(r.status, xStat, y, tableFont)
                y += 22
            }
        } else {
            val xDate = 20f
            val xPer = 120f
            val xDay = 190f
            val xTariff = 290f
            val xAmount = 410f
            val xStat = 520f

            canvas.drawText(pdfTableDate, xDate, y, headerFont)
            canvas.drawText(pdfTablePeriod, xPer, y, headerFont)
            canvas.drawText(nextPaymentPdf, xDay, y, headerFont)
            canvas.drawText(tariff, xTariff, y, headerFont)
            canvas.drawText(amount, xAmount, y, headerFont)
            canvas.drawText(pdfTableStatus, xStat, y, headerFont)
            y += 25

            for (r in records) {
                canvas.drawText(r.dateTime.substringBefore(" "), xDate, y, tableFont)
                canvas.drawText(r.periodMonths, xPer, y, tableFont)
                canvas.drawText(r.nextPayment, xDay, y, tableFont)
                canvas.drawText(r.tariff, xTariff, y, tableFont)
                canvas.drawText(r.amount, xAmount, y, tableFont)
                canvas.drawText(r.status, xStat, y, tableFont)
                y += 22
            }
        }

        document.finishPage(page)

        val file = File(cacheDir, "history_${System.currentTimeMillis()}.pdf")
        document.writeTo(java.io.FileOutputStream(file))
        document.close()
        return file
    }

    private fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, sendPdf))
    }

    private fun getServiceName(serviceKey: String): String {
        return serviceDisplayNames[serviceKey] ?: serviceKey
    }

    override suspend fun exportAllHistoryPdf(context: Context) {
        withContext(Dispatchers.IO) {
            val allKeys = listOf(
                ServiceKeys.ELECTRICITY, ServiceKeys.GAS, ServiceKeys.COLDWATER,
                ServiceKeys.HOTWATER, ServiceKeys.DRAINAGE,
                ServiceKeys.GARBAGE, ServiceKeys.ZONT, ServiceKeys.INTERNET,
                ServiceKeys.MTS, ServiceKeys.TINKOFF, ServiceKeys.TAXES,
                ServiceKeys.TROYKA, ServiceKeys.OSAGO, ServiceKeys.HOSTEL,
                ServiceKeys.CAPITAL_REPAIR
            )

            val allRecords = mutableListOf<Pair<String, HistoryRecord>>()
            val accountMap = mutableMapOf<String, String>()

            for (key in allKeys) {
                val historyText = fileManager.readHistory(key)
                if (historyText.isBlank()) continue

                val serviceName = accountPrefs.getCustomName(key).ifBlank {
                    getServiceName(key)
                }
                val accountNumber = accountPrefs.getAccount(key)
                accountMap[serviceName] = accountNumber
                val records = historyParser.parse(historyText, key)
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
        records: List<Pair<String, HistoryRecord>>,
        accountMap: Map<String, String>
    ): File {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842

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

        canvas.drawText(pdfAllHistoryTitle, leftMargin, y, titlePaint)
        y += 35
        val dateStr = SimpleDateFormat(dateFormatPattern, Locale.getDefault()).format(Date())
        canvas.drawText(pdfGenerated.format(dateStr), leftMargin, y, infoFont)
        y += 30

        val grouped = records.groupBy { it.first }

        val serviceOrderKeys = listOf(
            ServiceKeys.ELECTRICITY, ServiceKeys.COLDWATER, ServiceKeys.HOTWATER,
            ServiceKeys.DRAINAGE, ServiceKeys.GAS, ServiceKeys.GARBAGE,
            ServiceKeys.ZONT, ServiceKeys.INTERNET, ServiceKeys.MTS,
            ServiceKeys.TINKOFF, ServiceKeys.TAXES, ServiceKeys.TROYKA,
            ServiceKeys.OSAGO, ServiceKeys.HOSTEL, ServiceKeys.CAPITAL_REPAIR
        )
        val orderMap = serviceOrderKeys.withIndex().associate { it.value to it.index }
        val sortedGroups = grouped.entries.sortedBy { orderMap[it.key] ?: Int.MAX_VALUE }

        val xDate = 40f
        val xAmount = 200f
        val xStatus = 370f

        fun drawColumnHeaders(canvas: Canvas, yPos: Float) {
            canvas.drawText(pdfTableDate, xDate, yPos, headerFont)
            canvas.drawText(amount, xAmount, yPos, headerFont)
        }

        for ((serviceName, serviceRecords) in sortedGroups) {
            val neededHeight = 22f + 20f + 22f
            if (y + neededHeight > bottomLimit) {
                document.finishPage(currentPage.second)
                currentPage = createNewPage()
                canvas = currentPage.first
                y = 40f
            }

            val account = accountMap[serviceName]?.takeIf { it.isNotBlank() }
            if (account != null) {
                canvas.drawText(serviceName, leftMargin, y, serviceHeaderFont)
                val serviceNameWidth = serviceHeaderFont.measureText(serviceName)
                val accountLabel = personalAccountLabel
                val labelWidth = accountPaint.measureText(accountLabel)
                val gap = 10f
                canvas.drawText(accountLabel, leftMargin + serviceNameWidth + gap, y, accountPaint)
                canvas.drawText(account, leftMargin + serviceNameWidth + gap + labelWidth + 2f, y, accountPaint)
            } else {
                canvas.drawText(serviceName, leftMargin, y, serviceHeaderFont)
            }
            y += 22
            drawColumnHeaders(canvas, y)
            y += 20

            val dateFormat = SimpleDateFormat(dateFormatPattern, Locale.getDefault())
            val sortedRecords = serviceRecords.sortedByDescending { record ->
                try {
                    dateFormat.parse(record.second.dateTime)?.time ?: 0L
                } catch (_: Exception) {
                    0L
                }
            }

            for ((_, record) in sortedRecords) {
                if (y + 22f > bottomLimit) {
                    document.finishPage(currentPage.second)
                    currentPage = createNewPage()
                    canvas = currentPage.first
                    y = 40f
                    drawColumnHeaders(canvas, y)
                    y += 20
                }
                canvas.drawText(record.dateTime.substringBefore(" "), xDate, y, tableFont)
                val money = record.amount.ifBlank { record.tariff }
                canvas.drawText(money, xAmount, y, tableFont)
                canvas.drawText(record.status, xStatus, y, tableFont)
                y += 22
            }

            y += 12
        }

        document.finishPage(currentPage.second)

        val file = File(cacheDir, "history_all_${System.currentTimeMillis()}.pdf")
        document.writeTo(java.io.FileOutputStream(file))
        document.close()
        return file
    }
}
