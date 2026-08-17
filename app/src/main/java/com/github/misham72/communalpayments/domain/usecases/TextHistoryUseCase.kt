package com.github.misham72.communalpayments.domain.usecases

import android.content.Context
import com.github.misham72.communalpayments.domain.repository.TextHistoryRepository

class TextHistoryUseCase(private val textHistoryRepository: TextHistoryRepository) {
    suspend fun shareSingleHistory(context: Context, serviceKey: String){
        textHistoryRepository.shareSingleHistory(context,serviceKey)
    }
}
