package com.github.misham72.communalpayments.data.repository.PeriodicRepository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepository
import com.github.misham72.communalpayments.domain.model.GarbageData
import com.github.misham72.communalpayments.domain.repository.GarbageRepository
import com.github.misham72.communalpayments.domain.utils.ServiceKeys

class GarbageRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepository(context, fileManager), GarbageRepository {
    override suspend fun saveGarbagePayment(data: GarbageData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = ServiceKeys.GARBAGE
        val content = formatPeriodicPayment(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_garbage),
            nextPayment = data.nextPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            periodMonths = data.periodMonths  // ← передаём период
        )
        fileManager.appendRecord(serviceKey, content)
    }
}
