package com.github.misham72.communalpayments.presentation.screen.screens.garbage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.model.periodic.PeriodicData
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.PeriodicDataCollector
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.common.UiMessages
import com.github.misham72.communalpayments.presentation.utils.HistoryExporter
import com.github.misham72.communalpayments.presentation.utils.PdfHistoryExporter
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
    private val periodicDataCollector: PeriodicDataCollector,
    private val settingsRepository: UserSettingsRepository,
    private val repository: IProviderRepository
) : ViewModel() {

    companion object {
        const val SERVICE_KEY = ServiceKeys.GARBAGE
    }

    // 1️⃣ СОСТОЯНИЕ ЭКРАНА (что храним) - что отображать ?
    data class UiState(
        val paymentDay: String = "",
        val periodMonths: String = "",
        val providerDetails: ProviderDetails = ProviderDetails(),
        val showAccountDialog: Boolean = false,
        val customDate: String = "",
        val result: PeriodicData? = null,
        val error: ValidationError? = null,
    )

    private val _uiState = MutableStateFlow(UiState())//когда отображать и как часто ?
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Загружаем всё параллельно, если возможно
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.GARBAGE) }
            val saveDay = settingsRepository.getPaymentDay(SERVICE_KEY) ?: ""
            val savePeriod = settingsRepository.getPeriodMonths(SERVICE_KEY) ?: ""
            val savedTariff = settingsRepository.getTariff(SERVICE_KEY) ?: ""
            val savedDate = settingsRepository.getCustomDate(SERVICE_KEY)

            val details = detailsDeferred.await() // ждём результат

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
        }
    }
    //Это функция для сохранения данных провайдера (реквизитов) в постоянное хранилище и обновления UI

    fun saveProviderDetails(details: ProviderDetails) {
        viewModelScope.launch {
            repository.saveProviderDetails(ServiceKeys.GARBAGE, details)// 1) Сохраняем в SharedPreferences
            _uiState.update { it.copy(providerDetails = details) }// 2) Обновляем состояние экрана
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

    // 2️⃣ ПОЛУЧАЕМ ВВОД ОТ ПОЛЬЗОВАТЕЛЯ
    fun onPaymentDayChange(value: String) {
        _uiState.update { it.copy(paymentDay = value) }
    }

    fun onPeriodMonthsChange(value: String) {
        _uiState.update { it.copy(periodMonths = value) }
    }

    fun onPriceTariffChange(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                providerDetails = currentState.providerDetails.copy(tariff = value)
            )
        }
    }

    // 👇 НОВЫЙ МЕТОД
    private fun parseStartDate(dateString: String): Date {
        return try {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (_: Exception) {
            Date() // при ошибке используем текущую дату
        }
    }

    fun onCalculateClick() {
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
                val data = periodicDataCollector.collectPeriodicData(
                    serviceKey = ServiceKeys.GARBAGE,
                    isHistory = true,
                    paymentDay = paymentDay,
                    periodMonths = periodMonths,
                    startDate = startDate,
                    priceTariff = priceTariff,
                    accountNumber = account
                )

                // Обновляем UI – UseCase уже сохранил данные
                _uiState.update { state ->
                    state.copy(
                        result = data,
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
