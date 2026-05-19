package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.TaxesData


interface TaxesRepository {
    suspend fun saveTaxesPayment(data: TaxesData)
}
