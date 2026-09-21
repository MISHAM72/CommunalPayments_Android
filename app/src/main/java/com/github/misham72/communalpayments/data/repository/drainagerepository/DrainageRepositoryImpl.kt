package com.github.misham72.communalpayments.data.repository.drainagerepository

import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BaseRepository
import com.github.misham72.communalpayments.domain.constants.ServiceKeys

class DrainageRepositoryImpl(
    fileManager: FileManager,
    dateFormatPattern: String,
    private val personalAccountTemplate: String,
    private val consumptionTemplate: String,
    private val currencyTemplate: String,
    private val serviceName: String,
    private val unit: String
) : BaseRepository(fileManager, dateFormatPattern) {

    suspend fun save(
        accountNumber: String,
        coldUsage: Double,
        hotUsage: Double,
        totalUsage: Double,
        tariff: Double,
        payment: Double,
        periodMonths: Int = 1,
        nextPaymentDate: String = "",
        isHistory: Boolean
    ) {
        val dateTime = getCurrentDateTime()
        val content = buildString {
            if (isHistory) appendLine(historyHeader)
            appendLine(dateTime)
            appendLine(headerSeparator)
            appendLine(personalAccountTemplate + accountNumber)
            appendLine(serviceName)
            appendLine()
            appendLine("Период: $periodMonths мес.")
            appendLine("Дата оплаты: $nextPaymentDate")
            appendLine("ХВС: ${consumptionTemplate.format(coldUsage, unit)}")
            appendLine("ГВС: ${consumptionTemplate.format(hotUsage, unit)}")
            appendLine("Итого: ${consumptionTemplate.format(totalUsage, unit)}")
            appendLine("Тариф: $tariff")
            appendLine()
            appendLine(currencyTemplate.format(payment))
        }

        fileManager.appendRecord(ServiceKeys.DRAINAGE, content)
    }
}
