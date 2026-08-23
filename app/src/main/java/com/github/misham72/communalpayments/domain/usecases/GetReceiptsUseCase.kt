package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.model.Receipt
import com.github.misham72.communalpayments.domain.repository.ReceiptRepository

class GetReceiptsUseCase(
    private val receiptRepository: ReceiptRepository
) {
    suspend operator fun invoke(serviceKey: String): List<Receipt> {
        return receiptRepository.getReceipts(serviceKey)
    }
}
