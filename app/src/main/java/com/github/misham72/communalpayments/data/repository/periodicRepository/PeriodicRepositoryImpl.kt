package com.github.misham72.communalpayments.data.repository.periodicRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepository
import com.github.misham72.communalpayments.domain.model.periodic.PeriodicData
import com.github.misham72.communalpayments.domain.repository.PeriodicRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class PeriodicRepositoryImpl(
    context: Context,
    fileManager: FileManager
) : BasePeriodicRepository(context, fileManager), PeriodicRepository {

    override suspend fun save(data: PeriodicData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = data.serviceKey
        val serviceDisplayName = getServiceDisplayName(serviceKey, context)

        val content = formatPeriodicPayment(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = serviceDisplayName,
            nextPayment = data.nextPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            periodMonths = data.periodMonths
        )

        fileManager.appendRecord(serviceKey, content)
    }

    private fun getServiceDisplayName(serviceKey: String, context: Context): String {
        return when (serviceKey) {
            ServiceKeys.GARBAGE -> context.getString(R.string.service_display_name_garbage)
            ServiceKeys.INTERNET -> context.getString(R.string.service_display_name_internet)
            ServiceKeys.MTS -> context.getString(R.string.service_display_name_mts)
            ServiceKeys.HOSTEL -> context.getString(R.string.service_display_name_hostel)
            ServiceKeys.OSAGO -> context.getString(R.string.service_display_name_osago)
            ServiceKeys.TAXES -> context.getString(R.string.service_display_name_taxes)
            ServiceKeys.TINKOFF -> context.getString(R.string.service_display_name_tinkoff)
            ServiceKeys.TROYKA -> context.getString(R.string.service_display_name_troyka)
            ServiceKeys.ZONT -> context.getString(R.string.service_display_name_zont)
            else -> serviceKey
        }
    }
}
