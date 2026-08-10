package com.github.misham72.communalpayments.presentation.screen.screens.electricity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.exceptions.InvalidReadingException
import com.github.misham72.communalpayments.domain.common.DomainMessages
import com.github.misham72.communalpayments.domain.model.metric.ElectricityData
import com.github.misham72.communalpayments.domain.model.metric.MeterData
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.usecases.MeterDataCollector
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.utils.PdfHistoryExporter
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ElectricityViewModel(
    private val meterDataCollector: MeterDataCollector,
    private val accountPrefs: AccountPreferences,
    private val repository: IProviderRepository

) : ViewModel() {  //✅ Класс наследуется от ViewModel() — будет жить при поворотах

    companion object {
        const val SERVICE_KEY = ServiceKeys.ELECTRICITY
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
    )

    private val _uiState = MutableStateFlow(UiState())  //✅ MutableStateFlow для изменяемого состояния
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()  //✅ это StateFlow<UiState>, который хранит текущее состояние экрана.

    init {
        viewModelScope.launch {
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.ELECTRICITY) }
            val savedLastReading = accountPrefs.getLastReading(SERVICE_KEY)
            val savedTariff = accountPrefs.getTariff(SERVICE_KEY)
            val savedDate = accountPrefs.getCustomDate(SERVICE_KEY)

            val details = detailsDeferred.await()

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
            repository.saveProviderDetails(ServiceKeys.ELECTRICITY, details)
            _uiState.update { it.copy(providerDetails = details) }
            // Если нужно обновить другие поля (тариф и т.д.) – можно сделать здесь
        }
    }

    fun updateCustomDate(date: String) {
        _uiState.update { it.copy(customDate = date) }
        accountPrefs.saveCustomDate(SERVICE_KEY, date)
    }

    fun onPdfExport(context: Context) {
        viewModelScope.launch {
            PdfHistoryExporter.exportAndShare(context, SERVICE_KEY)
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
                val data = meterDataCollector.collectMeterData(
                    current = current,
                    previous = previous,
                    tariff = tariff,
                    accountNumber = account,
                    serviceKey = SERVICE_KEY,
                    factory = ::ElectricityData   // фабрика – конструктор ElectricityData
                )

                // После успешного сохранения обновляем UI
                _uiState.update { state ->
                    state.copy(
                        previousReading = state.currentReading,
                        currentReading = "",
                        result = data,
                        error = null
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
