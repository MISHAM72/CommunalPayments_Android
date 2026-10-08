package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.metric.GasNormData

interface GasNormRepository {
    suspend fun save(data: GasNormData)
}
