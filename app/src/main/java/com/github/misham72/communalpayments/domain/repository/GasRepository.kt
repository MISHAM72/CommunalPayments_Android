package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Gas

interface GasRepository {
    suspend fun saveGasPayment(data: Gas.GasData)
}