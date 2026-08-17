package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.repository.HistoryRepository

class GetHistoryUseCase(
    private val historyRepository: HistoryRepository
) {
    suspend fun getHistory(serviceKey: String): String {
        return historyRepository.readHistory(serviceKey)
    }
}
