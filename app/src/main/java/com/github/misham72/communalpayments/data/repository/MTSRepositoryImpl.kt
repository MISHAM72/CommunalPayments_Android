package com.github.misham72.communalpayments.data.repository


import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.repository.MTSRepository
import com.github.misham72.communalpayments.domain.userclasses.MTS


class MTSRepositoryImpl(
    context: Context, fileManager: FileManager
) : BasePeriodicRepositoryWithAccount(context, fileManager), MTSRepository
{

    override fun saveMTSPayment(data: MTS.MTSData) {
        val dateTime = getCurrentDateTime()
        val fileName = getFileName(context.getString(R.string.service_key_mts))
        val status = context.getString(R.string.status_calculated)

        val content = formatWithAccountNumber(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = context.getString(R.string.service_display_name_mts),
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