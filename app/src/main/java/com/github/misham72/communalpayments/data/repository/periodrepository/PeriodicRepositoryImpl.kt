package com.github.misham72.communalpayments.data.repository.periodrepository

import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.base.BasePeriodicRepository
import com.github.misham72.communalpayments.domain.model.periodic.PeriodicData
import com.github.misham72.communalpayments.domain.repository.PeriodicRepository

class PeriodicRepositoryImpl(
    fileManager: FileManager,
    dateFormatPattern: String,
    personalAccountTemplate: String,
    nextPaymentTemplate: String,
    priceTariffTemplate: String,
    periodMonthsTemplate: String,
    currencyTemplate: String,
    private val serviceDisplayNames: Map<String, String>
) : BasePeriodicRepository(
    fileManager,
    dateFormatPattern,
    personalAccountTemplate,
    nextPaymentTemplate,
    priceTariffTemplate,
    periodMonthsTemplate,
    currencyTemplate
), PeriodicRepository {

    override suspend fun save(data: PeriodicData) {
        val dateTime = getCurrentDateTime()
        val serviceKey = data.serviceKey
        val serviceDisplayName = serviceDisplayNames[serviceKey] ?: serviceKey

        val content = formatPeriodicPayment(
            accountNumber = data.accountNumber,
            dateTime = dateTime,
            serviceName = serviceDisplayName,
            nextPayment = data.nextPayment,
            priceTariff = data.priceTariff,
            isHistory = data.isHistory,
            periodMonths = data.periodMonths
        )

        fileManager.appendRecord(serviceKey, content)
    }
}
