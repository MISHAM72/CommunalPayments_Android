package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Tinkoff

interface TinkoffRepository {
    fun saveTinkoffPayment(data: Tinkoff.TinkoffData)
}