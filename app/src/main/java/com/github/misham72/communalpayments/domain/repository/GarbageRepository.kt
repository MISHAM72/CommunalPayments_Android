package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.GarbageData

interface GarbageRepository {
    suspend fun saveGarbagePayment(data: GarbageData)
}
