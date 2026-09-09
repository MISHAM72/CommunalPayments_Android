package com.github.misham72.communalpayments.presentation.screen.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.model.ExpenseSummary
import com.github.misham72.communalpayments.domain.usecases.GetExpensesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Year

data class ExpensesUiState(
    val isLoading: Boolean = true,
    val data: ExpenseSummary? = null,
    val error: String? = null
)

class ExpensesViewModel(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val defaultErrorMessage: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()
    fun loadSummary(serviceKeys: List<String>, year: Int = Year.now().value) {
        viewModelScope.launch {
            _uiState.value = ExpensesUiState()
            try {
                val map = getExpensesUseCase(serviceKeys, year)
                val byService = serviceKeys.associateWith { key ->
                    map[key]?.total ?: 0.0
                }
                val total = byService.values.sum()
                val summary = ExpenseSummary(total, byService)
                _uiState.value = ExpensesUiState(isLoading = false, data = summary)
            } catch (e: Exception) {
                _uiState.value = ExpensesUiState(
                    isLoading = false,
                    error = e.localizedMessage ?: defaultErrorMessage
                )
            }
        }
    }
}
