package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.common.DomainMessages
import com.github.misham72.communalpayments.domain.model.metric.GasNormData
import com.github.misham72.communalpayments.domain.repository.GasNormRepository

class GasNormUseCase(
    private val repository: GasNormRepository
) {
    suspend fun collectGasNorm(
        serviceKey: String,
        isHistory: Boolean,
        norm: Double,
        people: Int,
        tariff: Double
    ): GasNormData {
        require(norm > 0.0) { DomainMessages.TARIFF_MUST_BE_POSITIVE }
        require(people > 0) { DomainMessages.PEOPLE_MUST_BE_POSITIVE }
        require(tariff > 0.0) { DomainMessages.TARIFF_MUST_BE_POSITIVE }

        val payment = norm * people * tariff

        val data = GasNormData(
            serviceKey = serviceKey,
            isHistory = isHistory,
            norm = norm,
            people = people,
            tariff = tariff,
            payment = payment
        )

        repository.save(data)
        return data
    }
}
