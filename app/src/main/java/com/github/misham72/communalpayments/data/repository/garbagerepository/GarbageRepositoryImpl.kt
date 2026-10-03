package com.github.misham72.communalpayments.data.repository.garbagerepository

import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BaseRepository
import com.github.misham72.communalpayments.domain.model.periodic.GarbageData
import com.github.misham72.communalpayments.domain.model.periodic.GarbageMode
import com.github.misham72.communalpayments.domain.repository.GarbageRepository

class GarbageRepositoryImpl(
    fileManager: FileManager,
    dateFormatPattern: String,
    private val personalAccountTemplate: String,
    private val nextPaymentTemplate: String,
    private val periodMonthsTemplate: String,
    private val currencyTemplate: String,
    private val tariffPerSqmTemplate: String,
    private val tariffPerPersonTemplate: String,
    private val areaTemplate: String,
    private val residentsTemplate: String,
    private val serviceDisplayName: String
) : BaseRepository(fileManager, dateFormatPattern), GarbageRepository {

    override suspend fun save(data: GarbageData) {
        val dateTime = getCurrentDateTime()

        val content = buildString {
            if (data.isHistory)
                appendLine(historyHeader)
            appendLine(dateTime)
            appendLine(headerSeparator)
            appendLine(personalAccountTemplate + data.accountNumber)
            appendLine(serviceDisplayName)
            appendLine()
            appendLine(nextPaymentTemplate.format(data.nextPayment))
            appendLine(periodMonthsTemplate.format(data.periodMonths))

            when (data.mode) {
                GarbageMode.AREA -> {
                    appendLine(tariffPerSqmTemplate.format(data.priceTariff))
                    appendLine(areaTemplate.format(data.value))
                }

                GarbageMode.RESIDENTS -> {
                    appendLine(tariffPerPersonTemplate.format(data.priceTariff))
                    appendLine(residentsTemplate.format(data.value.toInt()))
                }
            }

            appendLine()
            appendLine(currencyTemplate.format(data.totalAmount))
        }

        fileManager.appendRecord(data.serviceKey, content)
    }
}
