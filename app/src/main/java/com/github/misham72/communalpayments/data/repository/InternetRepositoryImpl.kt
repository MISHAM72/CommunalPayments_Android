package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.model.InternetData
import com.github.misham72.communalpayments.domain.repository.InternetRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class InternetRepositoryImpl(
    context: Context,
    fileManager: FileManager
) : BasePeriodicRepositoryWithAccount(context, fileManager), InternetRepository {

    override suspend fun saveInternetPayment(data: InternetData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.INTERNET
        val status = context.getString(R.string.status_calculated)

        val content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_internet),
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
