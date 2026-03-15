package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Electricity

interface ElectricityRepository {
    fun saveElectricityPayment(data: Electricity.ElectricityData)
}