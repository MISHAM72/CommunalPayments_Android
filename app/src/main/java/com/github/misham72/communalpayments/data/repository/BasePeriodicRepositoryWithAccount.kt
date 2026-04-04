package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.repository.PeriodicAccountNumberSupport


abstract class BasePeriodicRepositoryWithAccount(
    context: Context, fileManager: FileManager
) : BasePeriodicRepository(context, fileManager), PeriodicAccountNumberSupport {


    override fun formatWithAccountNumber(
        accountNumber: String, dateTime: String, serviceName: String, previousPayment: String, daysFromPayment: Long, nextPayment: String, daysUntilPayment: Long, priceTariff: Double, isHistory: Boolean, customStatus: String
    ): String {

        val baseContent = formatPeriodicPayment(
            dateTime = dateTime,
            serviceName = serviceName,
            previousPayment = previousPayment,
            daysFromPayment = daysFromPayment,
            nextPayment = nextPayment,
            daysUntilPayment = daysUntilPayment,
            priceTariff = priceTariff,
            isHistory = isHistory,
            customStatus = customStatus
        )

        // Если номер задан, добавляем его в начало
        return if (accountNumber.isNotBlank()) {
            buildString {
                appendLine(context.getString(R.string.personal_account_in_text_history, accountNumber))
                append(baseContent)
            }
        } else {
            baseContent

        }
    }
}