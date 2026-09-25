package com.github.misham72.communalpayments.presentation.screen.screens.expensesincome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.model.ExpenseSummary
import com.github.misham72.communalpayments.domain.usecases.GetExpensesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Year

enum class Period { Year, Month }

data class ExpensesUiState(
    val isLoading: Boolean = true,
    val data: ExpenseSummary? = null,
    val error: String? = null,
    val period: Period = Period.Year,
    val selectedYear: Int = Year.now().value,
    val selectedMonth: Int = LocalDate.now().monthValue,
)

class ExpensesViewModel(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val defaultErrorMessage: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()

    private var currentServiceKeys: List<String> = emptyList()

    fun loadSummary(serviceKeys: List<String>, year: Int = Year.now().value) {
        currentServiceKeys = serviceKeys
        _uiState.update { it.copy(selectedYear = year) }
        loadInternal()
    }

    fun setPeriod(period: Period) {
        _uiState.update { it.copy(period = period) }
        loadInternal()
    }

    fun previousMonth() {
        _uiState.update { state ->
            val newMonth = if (state.selectedMonth == 1) 12 else state.selectedMonth - 1
            val newYear = if (state.selectedMonth == 1) state.selectedYear - 1 else state.selectedYear
            state.copy(selectedMonth = newMonth, selectedYear = newYear)
        }
        loadInternal()
    }

    fun nextMonth() {
        _uiState.update { state ->
            val newMonth = if (state.selectedMonth == 12) 1 else state.selectedMonth + 1
            val newYear = if (state.selectedMonth == 12) state.selectedYear + 1 else state.selectedYear
            state.copy(selectedMonth = newMonth, selectedYear = newYear)
        }
        loadInternal()
    }

    private fun loadInternal() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val state = _uiState.value
                val month = if (state.period == Period.Month) state.selectedMonth else null
                val map = getExpensesUseCase(
                    currentServiceKeys,
                    state.selectedYear,
                    month
                )
                val byService = currentServiceKeys.associateWith { key ->
                    map[key]?.total ?: 0.0
                }
                val total = byService.values.sum()
                val summary = ExpenseSummary(total, byService)
                _uiState.update {
                    it.copy(isLoading = false, data = summary, error = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: defaultErrorMessage
                    )
                }
            }
        }
    }
}

