package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.TinkoffData


interface TinkoffRepository {
    suspend fun saveTinkoffPayment(data: TinkoffData)
}
