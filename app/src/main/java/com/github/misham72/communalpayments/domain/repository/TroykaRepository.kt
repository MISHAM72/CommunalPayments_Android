package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.TroykaData


interface TroykaRepository {
    suspend fun saveTroykaPayment(data: TroykaData)
}
