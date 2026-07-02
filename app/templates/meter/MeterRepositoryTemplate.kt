package com.github.misham72.communalpayments.data.repository

import android.content.Context
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.domain.userclasses.%

SERVICE_NAME%

/**
 * ШАБЛОН для репозитория счетчика
 *
 * КАК ИСПОЛЬЗОВАТЬ:
 * 1. Замените %SERVICE_NAME% на название сервиса (Water, Gas)
 * 2. Замените %service_name% на название в нижнем регистре (water, gas)
 * 3. Скопируйте в data/repository/ как %SERVICE_NAME%Repository.kt
 */
class %SERVICE_NAME%Repository(
context: Context,
fileManager: FileManager
) : BaseMeterRepository(context, fileManager) {

    fun save%SERVICE_NAME%Payment(data: %SERVICE_NAME%.%SERVICE_NAME%Data) {
        val dateTime = getCurrentDateTime()
        val fileName = getFileName("%service_name%")

        val content = formatMeterPayment(
            dateTime = dateTime,
            serviceName = context.getString(R.string.% service_name %),
            current = data.current,
            previous = data.previous,
            tariff = data.tariff,
            consumption = data.consumption,
            payment = data.payment,
            isHistory = data.isHistory,
            customStatus = context.getString(R.string.status_paid)
        )

        fileManager.saveToFile(content, fileName)
    }
}
