package com.github.misham72.communalpayments.data.repository.periodicRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepository
import com.github.misham72.communalpayments.domain.model.OsagoData
import com.github.misham72.communalpayments.domain.repository.OsagoRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class OsagoRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepository(context, fileManager), OsagoRepository {
    override suspend fun saveOsagoPayment(data: OsagoData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.OSAGO
        val content = formatPeriodicPayment(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_osago),
            nextPayment = data.nextPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            periodMonths = data.periodMonths  // ← передаём период
        )
        fileManager.appendRecord(serviceKey, content)
    }
}
