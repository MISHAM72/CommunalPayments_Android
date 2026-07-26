package com.github.misham72.communalpayments.data.repository.PeriodicRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepository
import com.github.misham72.communalpayments.domain.model.ZONTData
import com.github.misham72.communalpayments.domain.repository.ZONTRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class ZONTRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepository(context, fileManager), ZONTRepository {

    override suspend fun saveZONTPayment(data: ZONTData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.ZONT
        val content = formatPeriodicPayment(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_zont),
            nextPayment = data.nextPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            periodMonths = data.periodMonths  // ← передаём период
        )
        fileManager.appendRecord(serviceKey, content)
    }
}
