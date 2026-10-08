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
import com.github.misham72.communalpayments.domain.usecases.PdfHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.MeterDataUseCase
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
import com.github.misham72.communalpayments.domain.constants.ServiceKeys
import com.github.misham72.communalpayments.domain.model.metric.GasMode
import com.github.misham72.communalpayments.domain.model.metric.GasNormData
import com.github.misham72.communalpayments.domain.usecases.GasNormUseCase
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GasViewModel(
    private val meterDataUseCase: MeterDataUseCase,
    private val gasNormUseCase: GasNormUseCase,
    private val meterRepository: MeterRepository,
    private val settingsRepository: UserSettingsRepository,
    private val repository: IProviderRepository,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val pdfHistoryUseCase: PdfHistoryUseCase,
    private val gson: Gson
) : ViewModel() {

    companion object {
        const val SERVICE_KEY = ServiceKeys.GAS
    }

    data class UiState(
        val currentReading: String = "",
        val previousReading: String = "",
        val mode: GasMode = GasMode.METER,                 // ← НОВОЕ
        val norm: String = "",                              // ← НОВОЕ
        val people: String = "",
        val providerDetails: ProviderDetails = ProviderDetails(),
        val showAccountDialog: Boolean = false,   // флаг для диалога
        val customDate: String = "",
        val result: MeterData? = null,
        val error: ValidationError? = null,
        val lastResult: MeterData? = null,
        val normResult: GasNormData? = null,                // ← НОВОЕ
        val lastNormResult: GasNormData? = null,
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

            val savedMode = try {
                GasMode.valueOf(settingsRepository.getGasMode())
            } catch (_: Exception) {
                GasMode.METER
            }
            val savedNorm = settingsRepository.getGasNorm()
            val savedPeople = settingsRepository.getGasPeople()

            _uiState.update { currentState ->
                currentState.copy(
                    providerDetails = details.copy(
                        tariff = savedTariff.ifBlank { details.tariff }
                    ),
                    previousReading = savedLastReading,
                    customDate = savedDate,
                    mode = savedMode,
                    norm = savedNorm,
                    people = savedPeople
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
            pdfHistoryUseCase.exportSingleHistoryPdf(context, SERVICE_KEY)
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

    fun onModeChange(mode: GasMode) {
        _uiState.update { it.copy(mode = mode, result = null, normResult = null, error = null) }
        viewModelScope.launch {
            settingsRepository.saveGasMode(mode.name)
        }
    }

    fun onNormChange(value: String) {
        _uiState.update { it.copy(norm = value) }
    }

    fun onPeopleChange(value: String) {
        _uiState.update { it.copy(people = value) }
    }

    fun onCalculateClick() {
        when (_uiState.value.mode) {
            GasMode.METER -> calculateMeter()
            GasMode.NORM -> calculateNorm()
        }
    }

    private fun calculateMeter() {
        val current = _uiState.value.currentReading.toDoubleOrNull()
        val previous = _uiState.value.previousReading.toDoubleOrNull()
        val tariff = _uiState.value.providerDetails.tariff.toDoubleOrNull()
        val account = _uiState.value.providerDetails.accountNumber

        if (current == null || previous == null || tariff == null) {
            _uiState.update { it.copy(error = ValidationError.InvalidInput) }
            return
        }

        viewModelScope.launch {
            try {
                val data = meterDataUseCase.collectMeterData(
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
                        previousReading = state.currentReading,
                        currentReading = "",
                        result = data,
                        error = null,
                        lastResult = data
                    )
                }
            } catch (e: InvalidReadingException) {
                _uiState.update {
                    it.copy(
                        error = ValidationError.DomainError(
                            e.message ?: DomainMessages.DEFAULT_VALIDATION_ERROR
                        ),
                        result = null
                    )
                }
            }
        }
    }

    private fun calculateNorm() {
        val norm = _uiState.value.norm.toDoubleOrNull()
        val people = _uiState.value.people.toIntOrNull()
        val tariff = _uiState.value.providerDetails.tariff.toDoubleOrNull()

        if (norm == null || people == null || tariff == null) {
            _uiState.update { it.copy(error = ValidationError.InvalidInput) }
            return
        }

        viewModelScope.launch {
            try {
                val data = gasNormUseCase.collectGasNorm(
                    serviceKey = SERVICE_KEY,
                    isHistory = true,
                    norm = norm,
                    people = people,
                    tariff = tariff
                )
                settingsRepository.saveGasNorm(norm.toString())
                settingsRepository.saveGasPeople(people.toString())
                settingsRepository.saveLastResult(SERVICE_KEY, gson.toJson(data))
                _uiState.update { state ->
                    state.copy(
                        normResult = data,
                        lastNormResult = data,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = ValidationError.DomainError(
                            e.message ?: DomainMessages.DEFAULT_VALIDATION_ERROR
                        ),
                        normResult = null
                    )
                }
            }
        }
    }
}

