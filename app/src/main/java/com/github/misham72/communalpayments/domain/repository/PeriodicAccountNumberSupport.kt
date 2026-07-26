package com.github.misham72.communalpayments.domain.repository

interface PeriodicAccountNumberSupport {
    fun formatWithAccountNumber(
        accountNumber: String,
        dateTime: String,
        serviceName: String,
        nextPayment: String,
        priceTariff: Double,
        isHistory: Boolean, customStatus: String,
        nextPaymentDate: String = "",
        periodMonths: String = ""
    ): String
}
