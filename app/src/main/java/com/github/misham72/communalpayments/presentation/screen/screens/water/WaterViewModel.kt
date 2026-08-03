package com.github.misham72.communalpayments.presentation.screen.screens.water

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.model.WaterData
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.WaterRepository
import com.github.misham72.communalpayments.domain.userclasses.Water
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.utils.HistoryExporter
import com.github.misham72.communalpayments.presentation.utils.PdfHistoryExporter
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class WaterViewModel(
    private val water: Water,
    private val waterRepository: WaterRepository,
    private val accountPrefs: AccountPreferences,
    private val repository: IProviderRepository
) : ViewModel() {
    companion object {
        const val SERVICE_KEY = ServiceKeys.WATER
    }

    data class UiState( //✅ UiState как единый источник правды для экрана
        val currentReading: String = "",
        val previousReading: String = "",
        val providerDetails: ProviderDetails = ProviderDetails(),
        val showAccountDialog: Boolean = false,
        val customDate: String = "",
        val result: WaterData? = null,
        val error: ValidationError? = null
    )

    private val _uiState = MutableStateFlow(UiState())  //✅ MutableStateFlow для изменяемого состояния
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()  //✅ StateFlow для неизменяемого публичного доступа

    init {
        viewModelScope.launch {
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.WATER) }
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
            repository.saveProviderDetails(ServiceKeys.WATER, details)
            _uiState.update { it.copy(providerDetails = details) }
            // Если нужно обновить другие поля (тариф и т.д.) – можно сделать здесь
        }
    }

    fun updateCustomDate(date: String) {
        _uiState.update { it.copy(customDate = date) }
        accountPrefs.saveCustomDate(SERVICE_KEY, date)
    }

    fun onShareClick(context: Context) {
        viewModelScope.launch {
            HistoryExporter.shareSingleHistory(context, SERVICE_KEY)
        }
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
        val data = water.collectWaterData(
            current, previous, tariff, accountNumber = account
        )

        viewModelScope.launch {
            waterRepository.saveWaterPayment(data)
            accountPrefs.saveLastReading(SERVICE_KEY, current.toString())
            accountPrefs.saveTariff(SERVICE_KEY, tariff.toString())

            // ✅ Переносим текущее показание в предыдущее прямо сейчас
            _uiState.update { currentState ->
                currentState.copy(
                    previousReading = currentState.currentReading, // перенос
                    currentReading = "",                           // очистка для следующего ввода
                    result = data,
                    error = null
                )
            }
        }
    }
}
