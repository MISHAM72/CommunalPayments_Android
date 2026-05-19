package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.MTSData


interface MTSRepository {
    suspend fun saveMTSPayment(data: MTSData)
}
