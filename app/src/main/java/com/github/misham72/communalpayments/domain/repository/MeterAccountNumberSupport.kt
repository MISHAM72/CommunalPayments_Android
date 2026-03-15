package com.github.misham72.communalpayments.domain.repository

interface MeterAccountNumberSupport {
    fun formatWithAccountNumber(
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
    ): String
}