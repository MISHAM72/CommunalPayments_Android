package com.github.misham72.communalpayments.presentation.screen.screens.heating

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.common.DomainMessages
import com.github.misham72.communalpayments.domain.constants.ServiceKeys
import com.github.misham72.communalpayments.domain.exceptions.InvalidReadingException
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.model.metric.HeatingMeterData
import com.github.misham72.communalpayments.domain.model.metric.HeatingMode
import com.github.misham72.communalpayments.domain.model.periodic.PeriodicData
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.MeterRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.MeterDataUseCase
import com.github.misham72.communalpayments.domain.usecases.PdfHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.PeriodicDataUseCase
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
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


class ZONTViewModel(
    private val periodicDataUseCase: PeriodicDataUseCase,
    private val meterDataUseCase: MeterDataUseCase,
    private val meterRepository: MeterRepository,
    private val settingsRepository: UserSettingsRepository,
    private val repository: IProviderRepository,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val pdfHistoryUseCase: PdfHistoryUseCase,
    private val gson: Gson

) : ViewModel() {

    companion object {
        const val SERVICE_KEY = ServiceKeys.ZONT
    }

    data class UiState(
        val paymentDay: String = "",      // день платежа
        val periodMonths: String = "",
        val currentReading: String = "",
        val previousReading: String = "",
        val providerDetails: ProviderDetails = ProviderDetails(),
        val showAccountDialog: Boolean = false,
        val customDate: String = "",
        val meterResult: HeatingMeterData? = null,
        val periodResult: PeriodicData? = null,
        val lastResult: PeriodicData? = null,
        val lastMeterResult: HeatingMeterData? = null,
        val error: ValidationError? = null,
        val mode: HeatingMode = HeatingMode.PERIOD,
        val displayResult: HeatingDisplayResult? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Загружаем всё параллельно, если возможно
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.ZONT) }
            val saveDay = settingsRepository.getPaymentDay(SERVICE_KEY) ?: ""
            val savePeriod = settingsRepository.getPeriodMonths(SERVICE_KEY) ?: ""
            val savedTariff = settingsRepository.getTariff(SERVICE_KEY) ?: ""
            val savedDate = settingsRepository.getCustomDate(SERVICE_KEY)
            val details = detailsDeferred.await() // ждём результат
            val savedJson = settingsRepository.getLastResult(SERVICE_KEY)
            val lastResult = savedJson?.let { gson.fromJson(it, PeriodicData::class.java) }
            // Загрузка последнего результата по счётчику
            val savedMeterJson = settingsRepository.getLastMeterResult(SERVICE_KEY)   // ← новый метод
            val lastMeterResult = savedMeterJson?.let { gson.fromJson(it, HeatingMeterData::class.java) }
            _uiState.update { it.copy(lastMeterResult = lastMeterResult) }
            _uiState.update { it.copy(lastResult = lastResult) }

            _uiState.update { currentState ->
                currentState.copy(
                    providerDetails = details.copy(
                        tariff = savedTariff.ifBlank { details.tariff }
                    ),
                    paymentDay = saveDay,
                    periodMonths = savePeriod,
                    customDate = savedDate
                )
            }
            updateDisplayResult()
        }
    }

    private fun updateDisplayResult() {
        _uiState.update { state ->
            val display = when (state.mode) {
                HeatingMode.METER -> state.lastMeterResult?.let { HeatingDisplayResult.Meter(it) }
                HeatingMode.PERIOD -> state.lastResult?.let { HeatingDisplayResult.Period(it) }
            }
            state.copy(displayResult = display)
        }
    }

    fun saveProviderDetails(details: ProviderDetails) {
        viewModelScope.launch {
            repository.saveProviderDetails(ServiceKeys.ZONT, details)
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

    // 2️⃣ ПОЛУЧАЕМ ВВОД ОТ ПОЛЬЗОВАТЕЛЯ
    fun onPaymentDayChange(value: String) {
        _uiState.update { it.copy(paymentDay = value) }
    }

    fun onPeriodMonthsChange(value: String) {
        _uiState.update { it.copy(periodMonths = value) }
    }

    fun onCurrentReadingChange(value: String) {
        _uiState.update { it.copy(currentReading = value) }
    }

    fun onPreviousReadingChange(value: String) {
        _uiState.update { it.copy(previousReading = value) }
    }

    fun onPriceTariffChange(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                providerDetails = currentState.providerDetails.copy(tariff = value)
            )
        }
    }

    fun onModeChange(mode: HeatingMode) {
        _uiState.update { it.copy(mode = mode, periodResult = null, meterResult = null, error = null) }
        updateDisplayResult()
        viewModelScope.launch {
            settingsRepository.saveHeatingMode(mode.name)
        }
    }


    private fun parseStartDate(dateString: String): Date {
        return try {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (_: Exception) {
            Date() // при ошибке используем текущую дату
        }
    }

    fun onCalculateClick() {
        when (_uiState.value.mode) {
            HeatingMode.PERIOD -> calculatePeriod()
            HeatingMode.METER -> calculateMeter()
        }
    }

    private fun calculatePeriod() {
        val paymentDay = _uiState.value.paymentDay.toIntOrNull()
        val periodMonths = _uiState.value.periodMonths.toIntOrNull()
        val priceTariff = _uiState.value.providerDetails.tariff.toDoubleOrNull()
        val account = _uiState.value.providerDetails.accountNumber

        if (paymentDay == null || periodMonths == null || priceTariff == null) {
            _uiState.update { it.copy(error = ValidationError.InvalidInput) }
            return
        }
        val startDate = parseStartDate(_uiState.value.customDate)

        viewModelScope.launch {
            try {
                val data = periodicDataUseCase.collectPeriodicData(
                    serviceKey = ServiceKeys.ZONT,
                    isHistory = true,
                    paymentDay = paymentDay,
                    periodMonths = periodMonths,
                    startDate = startDate,
                    priceTariff = priceTariff,
                    accountNumber = account
                )
                settingsRepository.saveLastResult(SERVICE_KEY, gson.toJson(data))
                _uiState.update { state ->
                    state.copy(
                        periodResult = data,
                        lastResult = data,
                        error = null,
                    )
                }
                updateDisplayResult()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = ValidationError.DomainError(e.message ?: UiMessages.ERROR_SAVING),
                        periodResult = null
                    )
                }
            }
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
                    factory = ::HeatingMeterData
                )
                val heatingData = data as HeatingMeterData
                settingsRepository.saveLastMeterResult(SERVICE_KEY, gson.toJson(heatingData))
                _uiState.update { state ->
                    state.copy(
                        previousReading = state.currentReading,
                        currentReading = "",
                        meterResult = heatingData,
                        lastMeterResult = heatingData,
                        error = null
                    )
                }
                updateDisplayResult()
            } catch (e: InvalidReadingException) {
                _uiState.update {
                    it.copy(
                        error = ValidationError.DomainError(e.message ?: DomainMessages.DEFAULT_VALIDATION_ERROR),
                        meterResult = null
                    )
                }
            }
        }
    }
}
