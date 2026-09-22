package com.github.misham72.communalpayments.data.repository.drainagerepository

import com.github.misham72.communalpayments.data.common.DataConstants
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
            appendLine(DataConstants.LABEL_PERIOD + periodMonths + DataConstants.LABEL_PERIOD_SUFFIX)
            appendLine(DataConstants.LABEL_NEXT_PAYMENT + nextPaymentDate)
            appendLine(DataConstants.LABEL_COLD_WATER + consumptionTemplate.format(coldUsage, unit))
            appendLine(DataConstants.LABEL_HOT_WATER + consumptionTemplate.format(coldUsage, unit))
            appendLine(DataConstants.LABEL_TOTAL + consumptionTemplate.format(totalUsage, unit))
            appendLine(DataConstants.LABEL_TARIFF + tariff)
            appendLine()
            appendLine(currencyTemplate.format(payment))
        }

        fileManager.appendRecord(ServiceKeys.DRAINAGE, content)
    }
}
