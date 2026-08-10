package com.github.misham72.communalpayments.data.repository.meterRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BaseMeterRepository
import com.github.misham72.communalpayments.domain.common.DomainMessages
import com.github.misham72.communalpayments.domain.model.metric.GasData
import com.github.misham72.communalpayments.domain.model.metric.MeterData
import com.github.misham72.communalpayments.domain.repository.MeterRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class GasRepositoryImpl(
    context: Context,
    fileManager: FileManager
) : BaseMeterRepository(context, fileManager), MeterRepository {
    override suspend fun save(data: MeterData) {
        require(data is GasData) { DomainMessages.EXPECTED_GAS_DATA }
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.GAS
        val content = formatMeterPayment(
            accountNumber = data.accountNumber.value,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_gas),
            current = data.current.value,
            previous = data.previous.value,
            tariff = data.tariff.value,
            consumption = data.consumption.value,
            payment = data.payment.amount,
            isHistory = data.isHistory,
            unit = context.getString(R.string.unit_cubic_meter)
        )

        fileManager.appendRecord(serviceKey, content)
    }
}
