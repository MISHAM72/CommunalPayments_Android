package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.GasData


interface GasRepository {
    suspend fun saveGasPayment(data: GasData)
}
