package com.github.misham72.communalpayments.domain.repository

interface HistoryRepository {
    suspend fun readHistory(serviceKey: String): String
    suspend fun saveHistory(serviceKey: String, content: String)
}
