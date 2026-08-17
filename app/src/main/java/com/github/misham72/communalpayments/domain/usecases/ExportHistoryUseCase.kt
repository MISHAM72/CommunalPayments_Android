package com.github.misham72.communalpayments.domain.usecases

import android.content.Context
import com.github.misham72.communalpayments.domain.repository.PdfHistoryRepository

class ExportHistoryUseCase(
    private val pdfHistoryRepository: PdfHistoryRepository
) {
    suspend fun exportAndShare(context: Context, serviceKey: String) {
        pdfHistoryRepository.exportAndShare(context, serviceKey)
    }

    suspend fun exportAllHistoryPdf(context: Context) {
        pdfHistoryRepository.exportAllHistoryPdf(context)
    }
}
