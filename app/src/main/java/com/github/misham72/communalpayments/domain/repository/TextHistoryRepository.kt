package com.github.misham72.communalpayments.domain.repository

import android.content.Context

interface TextHistoryRepository {
    suspend fun shareSingleHistory(context: Context, serviceKey: String)
}
