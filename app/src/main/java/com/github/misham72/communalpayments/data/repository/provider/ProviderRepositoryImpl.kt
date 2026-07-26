package com.github.misham72.communalpayments.data.repository.provider

import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.repository.IProviderRepository

class ProviderRepositoryImpl(
    private val prefs: AccountPreferences
) : IProviderRepository {

    override suspend fun loadProviderDetails(serviceKey: String): ProviderDetails {
        return ProviderDetails(
            customServiceName = prefs.getCustomName(serviceKey),
            accountNumber = prefs.getAccount(serviceKey),
            tariff = prefs.getTariff(serviceKey),
            nameCompany = prefs.getNameCompany(serviceKey),
            inn = prefs.getInn(serviceKey),
            bankAccount = prefs.getBankAccount(serviceKey),
            websiteUrl = prefs.getWebsiteUrl(serviceKey)
        )
    }

    override suspend fun saveProviderDetails(serviceKey: String, details: ProviderDetails) {

        prefs.saveCustomName(serviceKey, details.customServiceName)
        prefs.saveAccount(serviceKey, details.accountNumber)
        prefs.saveTariff(serviceKey, details.tariff)
        prefs.saveNameCompany(serviceKey, details.nameCompany)
        prefs.saveInn(serviceKey, details.inn)
        prefs.saveBankAccount(serviceKey, details.bankAccount)
        prefs.saveWebsiteUrl(serviceKey, details.websiteUrl)
    }
}
