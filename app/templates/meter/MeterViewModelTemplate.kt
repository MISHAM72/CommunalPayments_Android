package com.github.misham72.communalpayments.presentation.screen.screens.%

service_name%

import androidx.lifecycle.ViewModel
import com.github.misham72.communalpayments.data.repository.%

SERVICE_NAME%Repository
import com.github.misham72.communalpayments.domain.userclasses.%

SERVICE_NAME%
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ШАБЛОН для ViewModel счетчика
 */
class %SERVICE_NAME%ViewModel(
private val %service_name%: %SERVICE_NAME%,
private val %service_name%Repository: %SERVICE_NAME%Repository
) : ViewModel() {

    data class UiState(
        val currentReading: String = "",
        val previousReading: String = "",
        val tariff: String = "",
        val result: %SERVICE_NAME%.%SERVICE_NAME%Data? = null,
    val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onCurrentReadingChange(value: String) {
        _uiState.update { it.copy(currentReading = value) }
    }

    fun onPreviousReadingChange(value: String) {
        _uiState.update { it.copy(previousReading = value) }
    }

    fun onTariffChange(value: String) {
        _uiState.update { it.copy(tariff = value) }
    }

    fun onCalculateClick() {
        val current = _uiState.value.currentReading.toDoubleOrNull()
        val previous = _uiState.value.previousReading.toDoubleOrNull()
        val tariff = _uiState.value.tariff.toDoubleOrNull()

        if (current == null || previous == null || tariff == null) {
            _uiState.update { it.copy(errorMessage = "Invalid input") }
            return
        }

        val data = %service_name%.collect%SERVICE_NAME%Data(current, previous, tariff)
        %service_name % Repository.save % SERVICE_NAME % Payment(data)
        _uiState.update { it.copy(result = data, errorMessage = null) }
    }
}
