package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.repository.OsagoRepository
import com.github.misham72.communalpayments.domain.userclasses.Osago

class OsagoRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepositoryWithAccount(context, fileManager), OsagoRepository {
    override fun saveOsagoPayment(data: Osago.OsagoData) {
        val dateTime = getCurrentDateTime()
        val fileName = getFileName(context.getString(R.string.service_key_osago))
        val status = context.getString(R.string.status_calculated)

        val content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_osago),
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