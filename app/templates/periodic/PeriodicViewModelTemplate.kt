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
 * ШАБЛОН для ViewModel периодического платежа
 *
 * КАК ИСПОЛЬЗОВАТЬ:
 * 1. Замените %SERVICE_NAME% на название сервиса (Internet, MTS, Taxes)
 * 2. Замените %service_name% на название в нижнем регистре (internet, mts, taxes)
 * 3. Скопируйте в presentation/screen/screens/%service_name%/ как %SERVICE_NAME%ViewModel.kt
 */
class %SERVICE_NAME%ViewModel(
private val %service_name%: %SERVICE_NAME%,
private val %service_name%Repository: %SERVICE_NAME%Repository
) : ViewModel() {

    data class UiState(
        val paymentAmount: String = "",
        val result: %SERVICE_NAME%.%SERVICE_NAME%Data? = null,
    val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onPaymentAmountChange(value: String) {
        _uiState.update { it.copy(paymentAmount = value) }
    }

    fun onCalculateClick() {
        val amount = _uiState.value.paymentAmount.toDoubleOrNull()

        if (amount == null) {
            _uiState.update { it.copy(errorMessage = "Invalid input") }
            return
        }

        // 1. Бизнес-логика
        val data = %service_name%.collect%SERVICE_NAME%Data(amount)

        // 2. Сохранение через репозиторий
        %service_name % Repository.save % SERVICE_NAME % Payment(data)

        // 3. Обновление UI
        _uiState.update { it.copy(result = data, errorMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
