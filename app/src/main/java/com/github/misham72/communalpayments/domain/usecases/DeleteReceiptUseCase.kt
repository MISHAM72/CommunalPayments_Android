package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.repository.ReceiptRepository

class DeleteReceiptUseCase(
    private val receiptRepository: ReceiptRepository
) {
    suspend operator fun invoke(id: String) {
        receiptRepository.deleteReceipt(id)
    }
}
