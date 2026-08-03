package com.github.misham72.communalpayments.presentation.screen.screens.mts

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.model.MTSData
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.MTSRepository
import com.github.misham72.communalpayments.domain.userclasses.MTS
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
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

class MTSViewModel(
    private val mts: MTS,                    // Домен
    private val mtsRepository: MTSRepository,
    private val accountPrefs: AccountPreferences,
    private val repository: IProviderRepository
) : ViewModel() {

    companion object {
        const val SERVICE_KEY = ServiceKeys.MTS
    }

    // 1️⃣ СОСТОЯНИЕ ЭКРАНА (что храним)
    data class UiState(
        val paymentDay: String = "",
        val periodMonths: String = "",
        val providerDetails: ProviderDetails = ProviderDetails(),
        val showAccountDialog: Boolean = false,   // флаг для диалога
        val customDate: String = "",
        val result: MTSData? = null,
        val error: ValidationError? = null,   // вместо errorMessage: String?
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Загружаем всё параллельно, если возможно
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.MTS) }
            val saveDay = accountPrefs.getPaymentDay(SERVICE_KEY)
            val savePeriod = accountPrefs.getPeriodMonths(SERVICE_KEY)
            val savedTariff = accountPrefs.getTariff(SERVICE_KEY)
            val savedDate = accountPrefs.getCustomDate(SERVICE_KEY)

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

    fun saveProviderDetails(details: ProviderDetails) {
        viewModelScope.launch {
            repository.saveProviderDetails(ServiceKeys.MTS, details)
            _uiState.update {
                it.copy(providerDetails = details)
            }
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

    private fun parseStartDate(dateString: String): Date {
        return try {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (_: Exception) {
            Date() // при ошибке используем текущую дату
        }
    }

    fun onCalculateClick() {
        // Валидация
        val paymentDay = _uiState.value.paymentDay.toIntOrNull()
        val periodMonths = _uiState.value.periodMonths.toIntOrNull()
        val priceTariff = _uiState.value.providerDetails.tariff.toDoubleOrNull()
        val account = _uiState.value.providerDetails.accountNumber
        if (paymentDay == null || periodMonths == null || priceTariff == null) {
            _uiState.update { it.copy(error = ValidationError.InvalidInput) }
            return
        }
        val startDate = parseStartDate(_uiState.value.customDate)

        // 1️⃣ Домен - ЧТО рассчитать
        val data = mts.collectMTSData(
            paymentDay = paymentDay,
            periodMonths = periodMonths,
            startDate = startDate,
            priceTariff = priceTariff,
            accountNumber = account
        )
        viewModelScope.launch {
            mtsRepository.saveMTSPayment(data)
            _uiState.update {
                accountPrefs.savePaymentDay(SERVICE_KEY, paymentDay.toString())
                accountPrefs.savePeriodMonths(SERVICE_KEY, periodMonths.toString())
                accountPrefs.saveTariff(SERVICE_KEY, priceTariff.toString())
                it.copy(result = data, error = null)

            }
        }
    }
}
