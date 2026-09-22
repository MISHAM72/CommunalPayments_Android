package com.github.misham72.communalpayments.data.repository.capitalrepair

import com.github.misham72.communalpayments.data.common.DataConstants
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BaseRepository
import com.github.misham72.communalpayments.domain.constants.ServiceKeys

class CapitalRepairRepositoryImpl(
    fileManager: FileManager,
    dateFormatPattern: String,
    private val personalAccountTemplate: String,
    private val areaTemplate: String,
    private val tariffTemplate: String,
    private val currencyTemplate: String,
    private val serviceName: String
) : BaseRepository(fileManager, dateFormatPattern) {

    suspend fun save(
        accountNumber: String,
        area: Double,
        tariff: Double,
        payment: Double,
        nextPayment: String,
        periodMonths: Int,
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
            appendLine(DataConstants.LABEL_NEXT_PAYMENT + nextPayment)
            appendLine(DataConstants.LABEL_PERIOD + periodMonths + DataConstants.LABEL_PERIOD_SUFFIX)
            appendLine(areaTemplate.format(area))
            appendLine(tariffTemplate.format(tariff))
            appendLine()
            appendLine(currencyTemplate.format(payment))
        }

        fileManager.appendRecord(ServiceKeys.CAPITAL_REPAIR, content)
    }
}
