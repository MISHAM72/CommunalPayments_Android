package com.github.misham72.communalpayments.data.repository.PeriodicRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepository
import com.github.misham72.communalpayments.domain.model.MTSData
import com.github.misham72.communalpayments.domain.repository.MTSRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class MTSRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepository(context, fileManager), MTSRepository {

    override suspend fun saveMTSPayment(data: MTSData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.MTS
        val content = formatPeriodicPayment(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_mts),
            nextPayment = data.nextPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            periodMonths = data.periodMonths  // ← передаём период
        )
        fileManager.appendRecord(serviceKey, content)
    }
}
