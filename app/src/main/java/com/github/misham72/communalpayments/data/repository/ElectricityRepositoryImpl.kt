package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.repository.ElectricityRepository
import com.github.misham72.communalpayments.domain.userclasses.Electricity
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class ElectricityRepositoryImpl(
    context: Context,
    fileManager: FileManager
) : BaseMeterRepositoryWithAccount(context, fileManager), ElectricityRepository {

    override suspend fun saveElectricityPayment(data: Electricity.ElectricityData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.ELECTRICITY
        val status = context.getString(R.string.status_calculated)

        val content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_electricity),
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