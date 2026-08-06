package com.github.misham72.communalpayments.presentation.screen.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.model.IncomeSummary
import com.github.misham72.communalpayments.domain.usecases.AddIncomeUseCase
import com.github.misham72.communalpayments.domain.usecases.GetYearlyIncomeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Year

data class IncomeUiState(
    val isLoading: Boolean = true,
    val summary: IncomeSummary? = null,
    val error: String? = null
)

class IncomeViewModel(
    private val getYearlyIncomeUseCase: GetYearlyIncomeUseCase,
    private val addIncomeUseCase: AddIncomeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    fun loadIncome(year: Int = Year.now().value) {
        viewModelScope.launch {
            _uiState.value = IncomeUiState()
            try {
                val summary = getYearlyIncomeUseCase(year)
                _uiState.value = IncomeUiState(isLoading = false, summary = summary)
            } catch (e: Exception) {
                @Suppress("HardcodedStringLiteral")
                _uiState.value = IncomeUiState(isLoading = false, error = e.localizedMessage ?: "Ошибка загрузки")
            }
        }
    }

    fun addIncome(source: String, amount: Double, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            // Не скрываем ошибку – пусть долетит до UI
            addIncomeUseCase(Year.now().value, date, source, amount)
            loadIncome(Year.now().value)
        }
    }
}
