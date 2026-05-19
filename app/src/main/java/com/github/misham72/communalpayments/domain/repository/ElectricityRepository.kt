package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.ElectricityData

interface ElectricityRepository {
    suspend fun saveElectricityPayment(data: ElectricityData)
}
