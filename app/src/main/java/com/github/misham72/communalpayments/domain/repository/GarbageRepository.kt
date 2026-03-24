package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Garbage

interface GarbageRepository {
    suspend fun saveGarbagePayment(data: Garbage.GarbageData)
}