package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Hostel

interface HostelRepository {
    suspend fun saveHostelPayment(data: Hostel.HostelData)
}