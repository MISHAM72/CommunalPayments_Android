package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.repository.MeterAccountNumberSupport

abstract class BaseMeterRepositoryWithAccount(
    context: Context,
    fileManager: FileManager
) : BaseMeterRepository(context, fileManager), MeterAccountNumberSupport {

    override fun formatWithAccountNumber(
        accountNumber: String,
        dateTime: String,
        serviceName: String,
        current: Double,
        previous: Double,
        tariff: Double,
        consumption: Double,
        payment: Double,
        isHistory: Boolean,
        customStatus: String
    ): String {
        // Сначала получаем базовое содержимое без номера (вызов метода родителя)
        val baseContent = formatMeterPayment(
            dateTime = dateTime,
            serviceName = serviceName,
            current = current,
            previous = previous,
            tariff = tariff,
            consumption = consumption,
            payment = payment,
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
