package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.metric.MeterData

interface MeterRepository {
    suspend fun save(data: MeterData)
}
