package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.Bank

interface BankRepository {
    fun getSupportedBanks(): List<Bank>
}
