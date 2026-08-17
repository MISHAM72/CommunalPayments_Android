package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.repository.HistoryRepository

class SaveHistoryUseCase(
    private val historyRepository: HistoryRepository
) {
    suspend fun saveHistory(serviceKey: String, content: String) {
        historyRepository.saveHistory(serviceKey, content)
    }
}
