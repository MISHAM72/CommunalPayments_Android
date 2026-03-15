package com.github.misham72.communalpayments.domain.repository

interface PeriodicAccountNumberSupport {
    fun formatWithAccountNumber(
        accountNumber: String,
        dateTime: String,
        serviceName: String,
        previousPayment: String,
        daysFromPayment: Long,       // дней прошло
        nextPayment: String,         // дата следующего
        daysUntilPayment: Long,      // дней осталось
        priceTariff: Double,          // тариф
        isHistory: Boolean, customStatus: String
    ): String
}