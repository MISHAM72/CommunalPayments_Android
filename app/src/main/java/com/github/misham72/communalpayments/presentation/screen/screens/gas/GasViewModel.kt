package com.github.misham72.communalpayments.presentation.screen.screens.gas

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.common.DomainMessages
import com.github.misham72.communalpayments.domain.exceptions.InvalidReadingException
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.model.metric.GasData
import com.github.misham72.communalpayments.domain.model.metric.MeterData
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.MeterRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.ExportHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.MeterDataCollector
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GasViewModel(
    private val meterDataCollector: MeterDataCollector,
    private val meterRepository: MeterRepository,
    private val settingsRepository: UserSettingsRepository,
    private val repository: IProviderRepository,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val exportHistoryUseCase: ExportHistoryUseCase,
    private val gson: Gson
) : ViewModel() {

    companion object {
        const val SERVICE_KEY = ServiceKeys.GAS
    }

    data class UiState(
        val currentReading: String = "",
        val previousReading: String = "",
        val providerDetails: ProviderDetails = ProviderDetails(),
        val showAccountDialog: Boolean = false,   // флаг для диалога
        val customDate: String = "",
        val result: MeterData? = null,
        val error: ValidationError? = null,
        val lastResult: MeterData? = null,
        val showLastResult: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())  //✅ MutableStateFlow для изменяемого состояния
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()  //✅ StateFlow для неизменяемого публичного доступа

    init {
        viewModelScope.launch {
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.GAS) }
            val savedLastReading = settingsRepository.getLastReading(SERVICE_KEY) ?: ""
            val savedTariff = settingsRepository.getTariff(SERVICE_KEY) ?: ""
            val savedDate = settingsRepository.getCustomDate(SERVICE_KEY)
            val details = detailsDeferred.await()
            val savedJson = settingsRepository.getLastResult(SERVICE_KEY)
            val lastResult = savedJson?.let { gson.fromJson(it, GasData::class.java) }
            _uiState.update { it.copy(lastResult = lastResult) }

            _uiState.update { currentState ->
                currentState.copy(
                    providerDetails = details.copy(
                        tariff = savedTariff.ifBlank { details.tariff }
                    ),
                    previousReading = savedLastReading,
                    customDate = savedDate
                )
            }
        }
    }

    fun saveProviderDetails(details: ProviderDetails) {
        viewModelScope.launch {
            repository.saveProviderDetails(ServiceKeys.GAS, details)
            _uiState.update { it.copy(providerDetails = details) }
            // Если нужно обновить другие поля (тариф и т.д.) – можно сделать здесь
        }
    }

    fun updateCustomDate(date: String) {
        _uiState.update { it.copy(customDate = date) }
        viewModelScope.launch {
            settingsRepository.saveCustomDate(SERVICE_KEY, date)
        }
    }

    fun onShareClick(context: Context) {
        viewModelScope.launch {
            textHistoryUseCase.shareSingleHistory(context, SERVICE_KEY)
        }
    }

    fun onPdfExport(context: Context) {
        viewModelScope.launch {
            exportHistoryUseCase.exportSingleHistoryPdf(context, SERVICE_KEY)
        }
    }

    fun openAccountDialog() {
        _uiState.update { it.copy(showAccountDialog = true) }
    }

    fun closeAccountDialog() {
        _uiState.update { it.copy(showAccountDialog = false) }
    }

    fun onCurrentReadingChange(value: String) {
        _uiState.update { it.copy(currentReading = value) }
    }

    fun onPreviousReadingChange(value: String) {
        _uiState.update { it.copy(previousReading = value) }
    }

    fun onTariffChange(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                providerDetails = currentState.providerDetails.copy(tariff = value)
            )
        }
    }

    fun onCalculateClick() {
        val current = _uiState.value.currentReading.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val previous = _uiState.value.previousReading.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val tariff = _uiState.value.providerDetails.tariff.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val account = _uiState.value.providerDetails.accountNumber
        //Если хоть одно поле пустое или содержит не число — показывает ошибку и останавливается.
        if (current == null || previous == null || tariff == null) {
            _uiState.update { it.copy(error = ValidationError.InvalidInput) }
            return
        }

        viewModelScope.launch {
            try {
                val data = meterDataCollector.collectMeterData(
                    repository = meterRepository,
                    current = current,
                    previous = previous,
                    tariff = tariff,
                    accountNumber = account,
                    serviceKey = SERVICE_KEY,
                    factory = ::GasData
                )
                settingsRepository.saveLastResult(SERVICE_KEY, gson.toJson(data))
                _uiState.update { state ->
                    state.copy(
                        previousReading = state.currentReading, // перенос
                        currentReading = "",                           // очистка для следующего ввода
                        result = data,
                        error = null,
                        lastResult = data
                    )
                }
            } catch (e: InvalidReadingException) {
                // 🛑 Ловим доменное исключение и показываем пользователю
                _uiState.update {
                    it.copy(
                        error = ValidationError.DomainError(e.message ?: DomainMessages.DEFAULT_VALIDATION_ERROR),
                        result = null
                    )
                }
            }
        }
    }
}
