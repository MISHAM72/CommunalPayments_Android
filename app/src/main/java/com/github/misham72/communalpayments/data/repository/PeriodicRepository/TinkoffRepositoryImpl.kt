package com.github.misham72.communalpayments.data.repository.PeriodicRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepositoryWithAccount
import com.github.misham72.communalpayments.domain.model.TinkoffData
import com.github.misham72.communalpayments.domain.repository.TinkoffRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class TinkoffRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepositoryWithAccount(context, fileManager), TinkoffRepository {

    override suspend fun saveTinkoffPayment(data: TinkoffData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.TINKOFF
        val status = context.getString(R.string.status_calculated)
        val content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_tinkoff),
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
