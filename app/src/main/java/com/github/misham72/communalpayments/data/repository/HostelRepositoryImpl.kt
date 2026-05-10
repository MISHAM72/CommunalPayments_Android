package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.repository.HostelRepository
import com.github.misham72.communalpayments.domain.userclasses.Hostel
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class HostelRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepositoryWithAccount(context, fileManager), HostelRepository {
    override suspend fun saveHostelPayment(data: Hostel.HostelData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.HOSTEL
        val status = context.getString(R.string.status_calculated)

        // ✅ Используем НОВЫЙ метод formatInternetPayment со всеми полями
        var content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_hostel),
            previousPayment = data.previousPayment,
            daysFromPayment = data.daysFromPayment,
            nextPayment = data.nextPayment,
            daysUntilPayment = data.daysUntilPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            customStatus = status
        )
        // Добавляем период, если он задан
        if (data.periodMonths.isNotBlank()) {
            content += context.getString(R.string.period_months_format)
        }
        fileManager.appendRecord(serviceKey, content)
    }
}