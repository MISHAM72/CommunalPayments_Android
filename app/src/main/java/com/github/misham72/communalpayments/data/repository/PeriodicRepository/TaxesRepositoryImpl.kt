package com.github.misham72.communalpayments.data.repository.PeriodicRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepositoryWithAccount
import com.github.misham72.communalpayments.domain.model.TaxesData
import com.github.misham72.communalpayments.domain.repository.TaxesRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class TaxesRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepositoryWithAccount(context, fileManager), TaxesRepository {
    override suspend fun saveTaxesPayment(data: TaxesData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.TAXES
        val status = context.getString(R.string.status_calculated)
        val content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_taxes),
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
