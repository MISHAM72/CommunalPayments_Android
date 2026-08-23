package com.github.misham72.communalpayments.domain.usecases

import com.github.misham72.communalpayments.domain.model.Receipt
import com.github.misham72.communalpayments.domain.repository.ReceiptRepository
import java.io.InputStream

class SaveReceiptUseCase(
    private val receiptRepository: ReceiptRepository
) {
    suspend operator fun invoke(serviceKey: String, inputStream: InputStream, fileName: String): Receipt {
        return receiptRepository.saveReceipt(serviceKey, inputStream, fileName)
    }
}
