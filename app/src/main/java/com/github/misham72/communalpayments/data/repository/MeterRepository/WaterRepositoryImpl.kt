package com.github.misham72.communalpayments.data.repository.MeterRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BaseMeterRepositoryWithAccount
import com.github.misham72.communalpayments.domain.model.WaterData
import com.github.misham72.communalpayments.domain.repository.WaterRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class WaterRepositoryImpl(
    context: Context, fileManager: FileManager
) : BaseMeterRepositoryWithAccount(context, fileManager), WaterRepository {

    override suspend fun saveWaterPayment(data: WaterData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.WATER
        val status = context.getString(R.string.status_calculated)

        val content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_water),
            current = data.current,
            previous = data.previous,
            tariff = data.tariff,
            consumption = data.consumption,
            payment = data.payment,
            isHistory = data.isHistory,
            customStatus = status
        )

        fileManager.appendRecord(serviceKey, content)
    }
}
