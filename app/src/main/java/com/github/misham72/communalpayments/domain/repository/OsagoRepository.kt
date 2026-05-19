package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.OsagoData


interface OsagoRepository {
    suspend fun saveOsagoPayment(data: OsagoData)
}
