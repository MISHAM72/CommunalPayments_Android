package com.github.misham72.communalpayments.presentation.screen.screens.analytics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.model.ExpenseSummary
import com.github.misham72.communalpayments.domain.userclasses.GetAllServicesYearlySummaryUseCase
import com.github.misham72.communalpayments.presentation.mapper.StatusDisplayMapper.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Year

data class AllServicesUiState(
    val isLoading: Boolean = true,
    val data: ExpenseSummary? = null,
    val error: String? = null
)

class AllServicesSummaryViewModel(
    private val getAllServicesYearlySummaryUseCase: GetAllServicesYearlySummaryUseCase,
    private val defaultErrorMessage: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(AllServicesUiState())
    val uiState: StateFlow<AllServicesUiState> = _uiState.asStateFlow()
    fun loadSummary(serviceKeys: List<String>, year: Int = Year.now().value) {
        viewModelScope.launch {
            _uiState.value = AllServicesUiState(isLoading = true)
            try {
                Log.d("Expenses", "serviceKeys: $serviceKeys")
                val map = getAllServicesYearlySummaryUseCase(serviceKeys, year)
                Log.d("Expenses", "Map from useCase: $map")
                val byService = serviceKeys.associateWith { key ->
                    map[key]?.total ?: 0.0
                }
                Log.d("Expenses", "byService after associateWith: $byService") // ← итоговая карта
                val total = byService.values.sum()
                val summary = ExpenseSummary(total, byService)
                _uiState.value = AllServicesUiState(isLoading = false, data = summary)
            } catch (e: Exception) {
                _uiState.value = AllServicesUiState(
                    isLoading = false,
                    error = e.localizedMessage ?: defaultErrorMessage
                )
            }
        }
    }
}
