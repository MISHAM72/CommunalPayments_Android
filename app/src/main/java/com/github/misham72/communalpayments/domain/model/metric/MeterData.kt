package com.github.misham72.communalpayments.domain.model.metric

import com.github.misham72.communalpayments.domain.model.valueobjects.AccountNumber
import com.github.misham72.communalpayments.domain.model.valueobjects.KilowattHour
import com.github.misham72.communalpayments.domain.model.valueobjects.Money
import com.github.misham72.communalpayments.domain.model.valueobjects.Tariff

interface MeterData {
    val current: KilowattHour
    val previous: KilowattHour
    val tariff: Tariff
    val accountNumber: AccountNumber
    val isHistory: Boolean
    val consumption: KilowattHour
    val payment: Money
}
