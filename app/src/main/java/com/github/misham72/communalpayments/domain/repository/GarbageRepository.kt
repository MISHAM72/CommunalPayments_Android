package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.periodic.GarbageData

interface GarbageRepository {
    suspend fun save(data: GarbageData)
}
