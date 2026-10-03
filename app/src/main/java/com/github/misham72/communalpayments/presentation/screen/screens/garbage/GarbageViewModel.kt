package com.github.misham72.communalpayments.presentation.screen.screens.garbage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.model.periodic.GarbageData
import com.github.misham72.communalpayments.domain.model.periodic.GarbageMode
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.GarbageDataUseCase
import com.github.misham72.communalpayments.domain.usecases.PdfHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
import com.github.misham72.communalpayments.domain.constants.ServiceKeys
import com.github.misham72.communalpayments.presentation.common.UiMessages
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GarbageViewModel(
    private val garbageDataUseCase: GarbageDataUseCase,
    private val settingsRepository: UserSettingsRepository,
    private val repository: IProviderRepository,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val pdfHistoryUseCase: PdfHistoryUseCase,
    private val gson: Gson
) : ViewModel() {

    companion object {
        const val SERVICE_KEY = ServiceKeys.GARBAGE
    }

    data class UiState(
        val paymentDay: String = "",
        val periodMonths: String = "",
        val mode: GarbageMode = GarbageMode.AREA,
        val value: String = "",
        val providerDetails: ProviderDetails = ProviderDetails(),
        val showAccountDialog: Boolean = false,
        val customDate: String = "",
        val result: GarbageData? = null,
        val lastResult: GarbageData? = null,
        val error: ValidationError? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.GARBAGE) }
            val saveDay = settingsRepository.getPaymentDay(SERVICE_KEY) ?: ""
            val savePeriod = settingsRepository.getPeriodMonths(SERVICE_KEY) ?: ""
            val savedTariff = settingsRepository.getTariff(SERVICE_KEY) ?: ""
            val savedDate = settingsRepository.getCustomDate(SERVICE_KEY)
            val savedMode = try {
                GarbageMode.valueOf(settingsRepository.getGarbageMode())
            } catch (_: Exception) {
                GarbageMode.AREA
            }
            val savedValue = settingsRepository.getGarbageValue()

            val details = detailsDeferred.await()
            val savedJson = settingsRepository.getLastResult(SERVICE_KEY)
            val lastResult = savedJson?.let { gson.fromJson(it, GarbageData::class.java) }
            _uiState.update { it.copy(lastResult = lastResult) }

            _uiState.update { currentState ->
                currentState.copy(
                    providerDetails = details.copy(
                        tariff = savedTariff.ifBlank { details.tariff }
                    ),
                    paymentDay = saveDay,
                    periodMonths = savePeriod,
                    mode = savedMode,
                    value = savedValue,
                    customDate = savedDate
                )
            }
        }
    }

    fun saveProviderDetails(details: ProviderDetails) {
        viewModelScope.launch {
            repository.saveProviderDetails(ServiceKeys.GARBAGE, details)
            _uiState.update { it.copy(providerDetails = details) }
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

    fun onPaymentDayChange(value: String) {
        _uiState.update { it.copy(paymentDay = value) }
    }

    fun onPeriodMonthsChange(value: String) {
        _uiState.update { it.copy(periodMonths = value) }
    }

    fun onModeChange(mode: GarbageMode) {
        _uiState.update { it.copy(mode = mode, value = "") }
        viewModelScope.launch {
            settingsRepository.saveGarbageMode(mode.name)
            settingsRepository.saveGarbageValue("")
        }
    }

    fun onValueChange(value: String) {
        _uiState.update { it.copy(value = value) }
    }

    fun onPriceTariffChange(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                providerDetails = currentState.providerDetails.copy(tariff = value)
            )
        }
    }

    private fun parseStartDate(dateString: String): Date {
        return try {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (_: Exception) {
            Date()
        }
    }

    fun onCalculateClick() {
        val paymentDay = _uiState.value.paymentDay.toIntOrNull()
        val periodMonths = _uiState.value.periodMonths.toIntOrNull()
        val priceTariff = _uiState.value.providerDetails.tariff.toDoubleOrNull()
        val value = _uiState.value.value.toDoubleOrNull()
        val account = _uiState.value.providerDetails.accountNumber
        val mode = _uiState.value.mode

        if (paymentDay == null || periodMonths == null || priceTariff == null || value == null) {
            _uiState.update { it.copy(error = ValidationError.InvalidInput) }
            return
        }

        val startDate = parseStartDate(_uiState.value.customDate)

        viewModelScope.launch {
            try {
                val data = garbageDataUseCase.collectGarbageData(
                    isHistory = true,
                    paymentDay = paymentDay,
                    periodMonths = periodMonths,
                    startDate = startDate,
                    mode = mode,
                    value = value,
                    priceTariff = priceTariff,
                    accountNumber = account
                )

                settingsRepository.saveLastResult(SERVICE_KEY, gson.toJson(data))
                settingsRepository.saveGarbageValue(value.toString())

                _uiState.update { state ->
                    state.copy(
                        result = data,
                        lastResult = data,
                        error = null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = ValidationError.DomainError(e.message ?: UiMessages.ERROR_SAVING),
                        result = null
                    )
                }
            }
        }
    }
}
