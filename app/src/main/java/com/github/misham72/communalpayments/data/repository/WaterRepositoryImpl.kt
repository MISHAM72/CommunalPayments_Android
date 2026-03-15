package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.repository.WaterRepository
import com.github.misham72.communalpayments.domain.userclasses.Water

class WaterRepositoryImpl(
    context: Context, fileManager: FileManager
) : BaseMeterRepositoryWithAccount(context, fileManager), WaterRepository {

    override fun saveWaterPayment(data: Water.WaterData) {
        val dateTime = getCurrentDateTime()
        val fileName = getFileName(context.getString(R.string.service_key_water))
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

        fileManager.saveToFile(content, fileName)
    }
}