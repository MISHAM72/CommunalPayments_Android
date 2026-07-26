package com.github.misham72.communalpayments.domain.model

//Вы создали Domain-модель – это контейнер для всех данных услуги.
data class ProviderDetails(
    val customServiceName: String = "",
    val accountNumber: String = "",
    val tariff: String = "",
    val nameCompany: String = "",
    val inn: String = "",
    val bankAccount: String = "",
    val websiteUrl: String = ""
)
