package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.periodic.PeriodicData

interface PeriodicRepository {
    suspend fun save(data: PeriodicData)
}
