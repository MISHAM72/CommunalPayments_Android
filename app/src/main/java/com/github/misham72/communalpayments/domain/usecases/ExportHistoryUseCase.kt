package com.github.misham72.communalpayments.domain.usecases

import android.content.Context
import com.github.misham72.communalpayments.domain.repository.PdfHistoryRepository

class ExportHistoryUseCase(
    private val pdfHistoryRepository: PdfHistoryRepository
) {
    suspend fun exportSingleHistoryPdf(context: Context, serviceKey: String) {
        pdfHistoryRepository.exportSingleHistoryPdf(context, serviceKey)
    }

    suspend fun exportAllHistoryPdf(context: Context) {
        pdfHistoryRepository.exportAllHistoryPdf(context)
    }
}
