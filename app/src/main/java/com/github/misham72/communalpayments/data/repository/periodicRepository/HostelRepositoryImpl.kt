package com.github.misham72.communalpayments.data.repository.periodicRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepository
import com.github.misham72.communalpayments.domain.model.HostelData
import com.github.misham72.communalpayments.domain.repository.HostelRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class HostelRepositoryImpl(
    context: Context,
    fileManager: FileManager
) : BasePeriodicRepository(context, fileManager), HostelRepository {

    override suspend fun saveHostelPayment(data: HostelData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.HOSTEL
        // ✅ Используем НОВЫЙ метод formatInternetPayment со всеми полями
        val content = formatPeriodicPayment(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_hostel),
            nextPayment = data.nextPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            periodMonths = data.periodMonths  // ← передаём период
        )
        fileManager.appendRecord(serviceKey, content)
    }
}
