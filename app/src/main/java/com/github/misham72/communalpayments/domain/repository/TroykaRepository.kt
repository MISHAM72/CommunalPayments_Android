package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Troyka

interface TroykaRepository {
    suspend fun saveTroykaPayment(data: Troyka.TroykaData)
}