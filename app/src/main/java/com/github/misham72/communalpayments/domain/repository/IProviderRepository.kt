package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.ProviderDetails

//Создать интерфейс репозитория (Domain-слой) – описать, как получать и сохранять эти данные.
interface IProviderRepository {
    suspend fun loadProviderDetails(serviceKey: String): ProviderDetails
    suspend fun saveProviderDetails(serviceKey: String, details: ProviderDetails)
}
