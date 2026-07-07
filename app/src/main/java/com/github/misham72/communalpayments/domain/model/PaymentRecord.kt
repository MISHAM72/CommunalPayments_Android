package com.github.misham72.communalpayments.domain.model

import java.time.YearMonth

data class PaymentRecord(
    val yearMonth: YearMonth,
    val amount: Double
)