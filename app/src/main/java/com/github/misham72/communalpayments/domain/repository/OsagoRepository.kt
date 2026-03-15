package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Osago

interface OsagoRepository {
    fun saveOsagoPayment(data: Osago.OsagoData)
}