package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.WaterData


interface WaterRepository {
    suspend fun saveWaterPayment(data: WaterData)
}
