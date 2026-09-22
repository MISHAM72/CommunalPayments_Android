package com.github.misham72.communalpayments.presentation.screen.screens.capitalrepair

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.data.repository.capitalrepair.CapitalRepairRepositoryImpl
import com.github.misham72.communalpayments.domain.constants.ServiceKeys
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.PdfHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
import com.github.misham72.communalpayments.presentation.common.UiMessages
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CapitalRepairViewModel(
    private val settingsRepository: UserSettingsRepository,
    private val repository: IProviderRepository,
    private val capitalRepairRepository: CapitalRepairRepositoryImpl,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val pdfHistoryUseCase: PdfHistoryUseCase,
    private val gson: Gson
) : ViewModel() {

    companion object {
        const val SERVICE_KEY = ServiceKeys.CAPITAL_REPAIR
    }

    data class UiState(
        val area: String = "",
        val paymentDay: String = "",
        val periodMonths: String = "",
        val providerDetails: ProviderDetails = ProviderDetails(),
        val showAccountDialog: Boolean = false,
        val customDate: String = "",
        val result: CapitalRepairResult? = null,
        val lastResult: CapitalRepairResult? = null,
        val error: ValidationError? = null,
    )

    data class CapitalRepairResult(
        val area: Double,
        val tariff: Double,
        val payment: Double,
        val nextPayment: String = "",
        val periodMonths: Int = 1,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.CAPITAL_REPAIR) }
            val savedArea = settingsRepository.getLastReading(SERVICE_KEY) ?: ""
            val saveDay = settingsRepository.getPaymentDay(SERVICE_KEY) ?: ""
            val savePeriod = settingsRepository.getPeriodMonths(SERVICE_KEY) ?: ""
            val savedTariff = settingsRepository.getTariff(SERVICE_KEY) ?: ""
            val savedDate = settingsRepository.getCustomDate(SERVICE_KEY)
            val details = detailsDeferred.await()
            val savedJson = settingsRepository.getLastResult(SERVICE_KEY)
            val lastResult = savedJson?.let { gson.fromJson(it, CapitalRepairResult::class.java) }
            _uiState.update { it.copy(lastResult = lastResult) }

            _uiState.update { currentState ->
                currentState.copy(
                    area = savedArea,
                    paymentDay = saveDay,
                    periodMonths = savePeriod,
                    providerDetails = details.copy(
                        tariff = savedTariff.ifBlank { details.tariff }
                    ),
                    customDate = savedDate
                )
            }
        }
    }

    fun onPaymentDayChange(value: String) {
        _uiState.update { it.copy(paymentDay = value) }
    }

    fun onPeriodMonthsChange(value: String) {
        _uiState.update { it.copy(periodMonths = value) }
    }

    fun saveProviderDetails(details: ProviderDetails) {
        viewModelScope.launch {
            repository.saveProviderDetails(ServiceKeys.CAPITAL_REPAIR, details)
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

    fun onAreaChange(value: String) {
        _uiState.update { it.copy(area = value) }
    }

    fun onTariffChange(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                providerDetails = currentState.providerDetails.copy(tariff = value)
            )
        }
    }

    fun onCalculateClick() {
        val area = _uiState.value.area.toDoubleOrNull()
        val tariff = _uiState.value.providerDetails.tariff.toDoubleOrNull()
        val account = _uiState.value.providerDetails.accountNumber
        val paymentDay = _uiState.value.paymentDay
        val periodMonths = _uiState.value.periodMonths.toIntOrNull() ?: 1

        if (area == null || tariff == null) {
            _uiState.update { it.copy(error = ValidationError.InvalidInput) }
            return
        }

        val payment = area * tariff

        viewModelScope.launch {
            try {
                capitalRepairRepository.save(
                    accountNumber = account,
                    area = area,
                    tariff = tariff,
                    payment = payment,
                    nextPayment = paymentDay,
                    periodMonths = periodMonths,
                    isHistory = true
                )
                val result = CapitalRepairResult(
                    area = area,
                    tariff = tariff,
                    payment = payment,
                    nextPayment = paymentDay,
                    periodMonths = periodMonths,
                )
                settingsRepository.saveLastReading(SERVICE_KEY, area.toString())
                settingsRepository.saveTariff(SERVICE_KEY, tariff.toString())
                settingsRepository.savePaymentDay(SERVICE_KEY, paymentDay)
                settingsRepository.savePeriodMonths(SERVICE_KEY, periodMonths.toString())
                settingsRepository.saveLastResult(SERVICE_KEY, gson.toJson(result))

                _uiState.update {
                    it.copy(result = result, lastResult = result, error = null)
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
