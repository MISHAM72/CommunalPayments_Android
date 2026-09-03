package com.github.misham72.communalpayments.data.repository.meterrepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BaseMeterRepository
import com.github.misham72.communalpayments.domain.common.DomainMessages
import com.github.misham72.communalpayments.domain.model.metric.MeterData
import com.github.misham72.communalpayments.domain.model.metric.WaterData
import com.github.misham72.communalpayments.domain.repository.MeterRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class WaterRepositoryImpl(
    fileManager: FileManager,
    dateFormatPattern: String,
    personalAccountTemplate: String,
    currentReadingTemplate: String,
    previousReadingTemplate: String,
    tariffTemplate: String,
    consumptionTemplate: String,
    currencyTemplate: String,
    private val serviceName: String,
    private val unit: String
) : BaseMeterRepository(
    fileManager,
    dateFormatPattern,
    personalAccountTemplate,
    currentReadingTemplate,
    previousReadingTemplate,
    tariffTemplate,
    consumptionTemplate,
    currencyTemplate
), MeterRepository {
    override suspend fun save(data: MeterData) {
        require(data is WaterData) { DomainMessages.EXPECTED_WATER_DATA }
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.WATER
        val content = formatMeterPayment(
            accountNumber = data.accountNumber.value,
            dateTime = dateTime,
            serviceName = serviceName,
            current = data.current.value,
            previous = data.previous.value,
            tariff = data.tariff.value,
            consumption = data.consumption.value,
            payment = data.payment.amount,
            isHistory = data.isHistory,
            unit = unit
        )

        fileManager.appendRecord(serviceKey, content)
    }
}
