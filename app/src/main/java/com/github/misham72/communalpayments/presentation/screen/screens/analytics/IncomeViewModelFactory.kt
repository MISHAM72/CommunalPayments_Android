package com.github.misham72.communalpayments.presentation.screen.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.domain.usecases.AddIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteAllIncomeRecordsBySourceUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteIncomeRecordUseCase
import com.github.misham72.communalpayments.domain.usecases.GetIncomeRecordsUseCase
import com.github.misham72.communalpayments.domain.usecases.GetYearlyIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.UpdateIncomeRecordUseCase

class IncomeViewModelFactory(
    private val getIncomeUseCase: GetYearlyIncomeUseCase,
    private val addIncomeUseCase: AddIncomeUseCase,
    private val getIncomeRecordsUseCase: GetIncomeRecordsUseCase,
    private val updateIncomeRecordUseCase: UpdateIncomeRecordUseCase,
    private val deleteIncomeRecordUseCase: DeleteIncomeRecordUseCase,
    private val deleteAllIncomeRecordsBySourceUseCase: DeleteAllIncomeRecordsBySourceUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncomeViewModel::class.java)) {
            return IncomeViewModel(
                getIncomeUseCase,
                addIncomeUseCase,
                getIncomeRecordsUseCase,
                updateIncomeRecordUseCase,
                deleteIncomeRecordUseCase,
                deleteAllIncomeRecordsBySourceUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
