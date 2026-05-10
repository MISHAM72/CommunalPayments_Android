package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.repository.TinkoffRepository
import com.github.misham72.communalpayments.domain.userclasses.Tinkoff
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class TinkoffRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepositoryWithAccount(context, fileManager), TinkoffRepository {

    override suspend fun saveTinkoffPayment(data: Tinkoff.TinkoffData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.TINKOFF
        val status = context.getString(R.string.status_calculated)
        var content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_tinkoff),
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