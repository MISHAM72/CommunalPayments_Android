package com.github.misham72.communalpayments.data.repository.MeterRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BaseMeterRepository
import com.github.misham72.communalpayments.domain.model.ElectricityData
import com.github.misham72.communalpayments.domain.repository.ElectricityRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class ElectricityRepositoryImpl(
    context: Context,
    fileManager: FileManager
) : BaseMeterRepository(context, fileManager), ElectricityRepository {

    override suspend fun saveElectricityPayment(data: ElectricityData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.ELECTRICITY
        val content = formatMeterPayment(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_electricity),
            current = data.current,
            previous = data.previous,
            tariff = data.tariff,
            consumption = data.consumption,
            payment = data.payment,
            isHistory = data.isHistory,
        )

        fileManager.appendRecord(serviceKey, content)
    }
}
