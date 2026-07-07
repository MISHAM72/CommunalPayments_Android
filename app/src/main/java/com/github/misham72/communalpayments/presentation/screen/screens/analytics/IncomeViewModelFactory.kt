package com.github.misham72.communalpayments.presentation.screen.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.domain.userclasses.AddIncomeUseCase
import com.github.misham72.communalpayments.domain.userclasses.GetYearlyIncomeUseCase
import com.github.misham72.communalpayments.domain.userclasses.UpdateIncomeUseCase

class IncomeViewModelFactory(
    private val getIncomeUseCase: GetYearlyIncomeUseCase,
    private val addIncomeUseCase: AddIncomeUseCase,
    private val updateIncomeUseCase: UpdateIncomeUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncomeViewModel::class.java)) {
            return IncomeViewModel(getIncomeUseCase, addIncomeUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
