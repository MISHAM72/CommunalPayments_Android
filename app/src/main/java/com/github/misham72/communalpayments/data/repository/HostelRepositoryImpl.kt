package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.model.HostelData
import com.github.misham72.communalpayments.domain.repository.HostelRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class HostelRepositoryImpl(
    context: Context,
    fileManager: FileManager
) : BasePeriodicRepositoryWithAccount(context, fileManager), HostelRepository {

    override suspend fun saveHostelPayment(data: HostelData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.HOSTEL
        val status = context.getString(R.string.status_calculated)

        // ✅ Используем НОВЫЙ метод formatInternetPayment со всеми полями
        val content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_hostel),
            previousPayment = data.previousPayment,
            daysFromPayment = data.daysFromPayment,
            nextPayment = data.nextPayment,
            daysUntilPayment = data.daysUntilPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            customStatus = status,
            nextPaymentDate = data.nextPayment,   // ← передаём день платежа
            periodMonths = data.periodMonths  // ← передаём период
        )
        fileManager.appendRecord(serviceKey, content)
    }
}
