package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Osago

interface OsagoRepository {
    suspend fun saveOsagoPayment(data: Osago.OsagoData)
}