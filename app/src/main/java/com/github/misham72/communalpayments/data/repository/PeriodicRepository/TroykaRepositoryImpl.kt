package com.github.misham72.communalpayments.data.repository.PeriodicRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepository
import com.github.misham72.communalpayments.domain.model.TroykaData
import com.github.misham72.communalpayments.domain.repository.TroykaRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class TroykaRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepository(context, fileManager), TroykaRepository {

    override suspend fun saveTroykaPayment(data: TroykaData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.TROYKA

        val content = formatPeriodicPayment(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_troyka),
            nextPayment = data.nextPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            periodMonths = data.periodMonths  // ← передаём период
        )
        fileManager.appendRecord(serviceKey, content)
    }
}
