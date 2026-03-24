package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.MTS

interface MTSRepository {
    suspend fun saveMTSPayment(data: MTS.MTSData)
}