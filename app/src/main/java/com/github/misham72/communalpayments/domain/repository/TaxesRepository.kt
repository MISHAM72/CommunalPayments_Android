package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Taxes

interface TaxesRepository {
    suspend fun saveTaxesPayment(data: Taxes.TaxesData)
}