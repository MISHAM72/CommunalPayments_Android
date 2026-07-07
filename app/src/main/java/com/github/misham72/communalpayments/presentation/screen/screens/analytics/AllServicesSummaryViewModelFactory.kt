package com.github.misham72.communalpayments.presentation.screen.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.domain.userclasses.GetAllServicesYearlySummaryUseCase

class AllServicesSummaryViewModelFactory(
    private val useCase: GetAllServicesYearlySummaryUseCase,
    private val defaultErrorMessage: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllServicesSummaryViewModel::class.java)) {
            return AllServicesSummaryViewModel(useCase, defaultErrorMessage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
