package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.HostelData


interface HostelRepository {
    suspend fun saveHostelPayment(data: HostelData)
}
