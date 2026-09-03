package com.github.misham72.communalpayments.domain.repository

import android.content.Context

interface PdfHistoryRepository {
    suspend fun exportSingleHistoryPdf(context: Context, serviceKey: String)
    suspend fun exportAllHistoryPdf(context: Context)
}
