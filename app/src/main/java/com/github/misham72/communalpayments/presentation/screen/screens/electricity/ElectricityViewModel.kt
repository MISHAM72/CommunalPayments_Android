package com.github.misham72.communalpayments.presentation.screen.screens.electricity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.common.DomainMessages
import com.github.misham72.communalpayments.domain.exceptions.InvalidReadingException
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.model.metric.ElectricityData
import com.github.misham72.communalpayments.domain.model.metric.MeterData
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.MeterRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.PdfHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.DataCollectorMeter
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ElectricityViewModel(
    private val dataCollectorMeter: DataCollectorMeter,
    private val meterRepository: MeterRepository,
    private val settingsRepository: UserSettingsRepository,
    private val repository: IProviderRepository,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val pdfHistoryUseCase: PdfHistoryUseCase,
    private val gson: Gson

) : ViewModel() {  //✅ Объявление класса ViewModel – чертёж будущих объектов.

    companion object {
        const val SERVICE_KEY = ServiceKeys.ELECTRICITY//– чертежи переменных.
    }

    /**Принимает пользовательский ввод (текущие показания, прошлые показания, тариф) и временно хранит их в UiState.*/
    data class UiState(
        val currentReading: String = "",
        val previousReading: String = "",
        val providerDetails: ProviderDetails = ProviderDetails(),  //Все реквизиты (название, ЛС, тариф, компания, ИНН, р/с) хранятся внутри этого объекта.
        val showAccountDialog: Boolean = false,   // флаг для диалога
        val customDate: String = "",
        val result: MeterData? = null,
        val error: ValidationError? = null,
        val lastResult: MeterData? = null,
        val showLastResult: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())  //✅ – чертежи переменных.
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()  //✅– чертежи переменных. Это StateFlow<UiState>, который хранит текущее состояние экрана.

    init {//– чертежи действий (инструкции).
        viewModelScope.launch {
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.ELECTRICITY) }
            val savedLastReading = settingsRepository.getLastReading(SERVICE_KEY) ?: ""
            val savedTariff = settingsRepository.getTariff(SERVICE_KEY) ?: ""
            val savedDate = settingsRepository.getCustomDate(SERVICE_KEY)
            val details = detailsDeferred.await()
            val savedJson = settingsRepository.getLastResult(SERVICE_KEY)
            val lastResult = savedJson?.let { gson.fromJson(it, ElectricityData::class.java) }
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

    fun saveProviderDetails(details: ProviderDetails) {//– чертежи действий (инструкции).
        viewModelScope.launch {
            repository.saveProviderDetails(ServiceKeys.ELECTRICITY, details)
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

    fun onCalculateClick() {//– чертежи действий (инструкции).
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
                // Вызываем новый UseCase – он создаст объект, сохранит, обновит Preferences
                val data = dataCollectorMeter.collectMeterData(
                    repository = meterRepository,
                    current = current,
                    previous = previous,
                    tariff = tariff,
                    accountNumber = account,
                    serviceKey = SERVICE_KEY,
                    factory = ::ElectricityData   // фабрика – конструктор ElectricityData
                )
// после получения data
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
                        error = ValidationError.DomainError(e.message ?: DomainMessages.DEFAULT_VALIDATION_ERROR),
                        result = null
                    )
                }
            }
        }
    }
}
