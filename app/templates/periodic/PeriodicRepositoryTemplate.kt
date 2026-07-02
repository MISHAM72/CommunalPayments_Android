package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.userclasses.%

SERVICE_NAME%

/**
 * ШАБЛОН для репозитория периодического платежа
 */
class %SERVICE_NAME%Repository(
context: Context,
fileManager: FileManager
) : BasePeriodicRepository(context, fileManager) {

    fun save%SERVICE_NAME%Payment(data: %SERVICE_NAME%.%SERVICE_NAME%Data) {
        val dateTime = getCurrentDateTime()
        val fileName = getFileName("%service_name%")

        val content = formatPeriodicPayment(
            dateTime = dateTime,
            serviceName = context.getString(R.string.% service_name %),
            payment = data.payment,
            isHistory = data.isHistory,
            customStatus = context.getString(R.string.status_paid)
        )

        fileManager.saveToFile(content, fileName)
    }
}
