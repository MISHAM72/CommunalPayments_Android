package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.repository.GarbageRepository
import com.github.misham72.communalpayments.domain.userclasses.Garbage

class GarbageRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepositoryWithAccount(context, fileManager), GarbageRepository {
    override fun saveGarbagePayment(data: Garbage.GarbageData) {
        val dateTime = getCurrentDateTime()
        val fileName = getFileName(context.getString(R.string.service_key_garbage))
        val status = context.getString(R.string.status_calculated)

        // ✅ Используем НОВЫЙ метод formatInternetPayment со всеми полями
        val content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_garbage),
            previousPayment = data.previousPayment,
            daysFromPayment = data.daysFromPayment,
            nextPayment = data.nextPayment,
            daysUntilPayment = data.daysUntilPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            customStatus = status
        )

        fileManager.saveToFile(content, fileName)
    }
}