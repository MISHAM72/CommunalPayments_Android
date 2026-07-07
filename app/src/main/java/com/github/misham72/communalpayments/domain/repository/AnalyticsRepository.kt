package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.YearSummary

interface AnalyticsRepository {
    suspend fun getYearSummary(serviceKey: String, year: Int): YearSummary

    // Новый метод – получить сводку по всем услугам
    suspend fun getAllServicesYearSummary(serviceKeys: List<String>, year: Int): Map<String, YearSummary>
}