package com.github.misham72.communalpayments.domain.model

import com.github.misham72.communalpayments.domain.model.valueobjects.AccountNumber
import com.github.misham72.communalpayments.domain.model.valueobjects.KilowattHour
import com.github.misham72.communalpayments.domain.model.valueobjects.Money
import com.github.misham72.communalpayments.domain.model.valueobjects.Tariff

data class ElectricityData(

    override val current: KilowattHour,
    override val previous: KilowattHour,
    override val tariff: Tariff,
    override val accountNumber: AccountNumber,
    override val isHistory: Boolean = false
) : MeterData {
    override val consumption: KilowattHour
        get() = current - previous

    override val payment: Money
        get() = consumption * tariff

    init {
        if (previous.value > 0.0) {
            require(current.value >= previous.value) {
                DomainMessages.CURRENT_LESS_THAN_PREVIOUS
            }
        }
    }
}
