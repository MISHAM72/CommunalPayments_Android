package com.github.misham72.communalpayments.data.repository.gasnormrepository

import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BaseRepository
import com.github.misham72.communalpayments.domain.model.metric.GasNormData
import com.github.misham72.communalpayments.domain.repository.GasNormRepository

class GasNormRepositoryImpl(
    fileManager: FileManager,
    dateFormatPattern: String,
    private val personalAccountTemplate: String,
    private val currencyTemplate: String,
    private val normTemplate: String,
    private val peopleTemplate: String,
    private val tariffTemplate: String,
    private val serviceDisplayName: String
) : BaseRepository(fileManager, dateFormatPattern), GasNormRepository {

    override suspend fun save(data: GasNormData) {
        val dateTime = getCurrentDateTime()

        val content = buildString {
            if (data.isHistory)
                appendLine(historyHeader)
            appendLine(dateTime)
            appendLine(headerSeparator)
            appendLine(personalAccountTemplate)
            appendLine(serviceDisplayName)
            appendLine()
            appendLine(normTemplate.format(data.norm))
            appendLine(peopleTemplate.format(data.people))
            appendLine(tariffTemplate.format(data.tariff))
            appendLine()
            appendLine(currencyTemplate.format(data.payment))
        }

        fileManager.appendRecord(data.serviceKey, content)
    }
}
