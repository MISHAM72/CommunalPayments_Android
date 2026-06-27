package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.repository.PeriodicAccountNumberSupport


abstract class BasePeriodicRepositoryWithAccount(
    context: Context, fileManager: FileManager
) : BasePeriodicRepository(context, fileManager), PeriodicAccountNumberSupport {


    override fun formatWithAccountNumber(
        accountNumber: String, dateTime: String, serviceName: String, previousPayment: String,
        daysFromPayment: Long, nextPayment: String, daysUntilPayment: Long, priceTariff: Double,
        isHistory: Boolean, customStatus: String, nextPaymentDate: String,
        periodMonths: String
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

        // Формируем content с учётом номера счёта
        var content = if (accountNumber.isNotBlank()) {
            buildString {
                appendLine(context.getString(R.string.personal_account_in_text_history, accountNumber))
                append(baseContent)
            }
        } else {
            baseContent
        }

        // ↓↓↓ ДОБАВЛЯЕМ ДЕНЬ ПЛАТЕЖА И ПЕРИОД ↓↓↓
        if (nextPaymentDate.isNotBlank()) {
            val day = extractDay(nextPaymentDate)
            if (day.isNotBlank()) {
                content += "\n" + context.getString(R.string.day_of_payment_format, day)
            }
        }
        if (periodMonths.isNotBlank()) {
            val months = periodMonths.toIntOrNull() ?: 0
            content += "\n" + context.getString(R.string.period_months_format, months)
        }
        // ↑↑↑ КОНЕЦ ДОБАВЛЕНИЯ ↑↑↑

        return content
    }

    // Вспомогательная функция (оставьте её в этом же классе)
    private fun extractDay(dateStr: String): String {
        return try {
            dateStr.split(".").firstOrNull() ?: ""
        } catch (_: Exception) {
            ""
        }
    }
}
