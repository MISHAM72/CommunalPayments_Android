package com.github.misham72.communalpayments.data.repository.PeriodicRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepository
import com.github.misham72.communalpayments.domain.model.TinkoffData
import com.github.misham72.communalpayments.domain.repository.TinkoffRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class TinkoffRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepository(context, fileManager), TinkoffRepository {

    override suspend fun saveTinkoffPayment(data: TinkoffData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.TINKOFF
        val content = formatPeriodicPayment(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_tinkoff),
            nextPayment = data.nextPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            periodMonths = data.periodMonths  // ← передаём период
        )
        fileManager.appendRecord(serviceKey, content)
    }
}
