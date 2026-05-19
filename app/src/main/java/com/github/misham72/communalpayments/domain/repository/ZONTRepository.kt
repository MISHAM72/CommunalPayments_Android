package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.ZONTData


interface ZONTRepository {
    suspend fun saveZONTPayment(data: ZONTData)
}
