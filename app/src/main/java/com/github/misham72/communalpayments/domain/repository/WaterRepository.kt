package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Water

interface WaterRepository {
    suspend fun saveWaterPayment(data: Water.WaterData)
}