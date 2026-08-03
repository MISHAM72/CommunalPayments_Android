package com.github.misham72.communalpayments.presentation.screen.screens.osago

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.model.OsagoData
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.OsagoRepository
import com.github.misham72.communalpayments.domain.userclasses.Osago
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

class OsagoViewModel(
    private val osago: Osago,                    // Домен
    private val osagoRepository: OsagoRepository,
    private val accountPrefs: AccountPreferences,
    private val repository: IProviderRepository
) : ViewModel() {

    companion object {
        const val SERVICE_KEY = ServiceKeys.OSAGO
    }

    data class UiState(
        val paymentDay: String = "",
        val periodMonths: String = "",
        val providerDetails: ProviderDetails = ProviderDetails(),
        val showAccountDialog: Boolean = false,
        val customDate: String = "",
        val result: OsagoData? = null,
        val error: ValidationError? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Загружаем всё параллельно, если возможно
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.OSAGO) }
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
            repository.saveProviderDetails(ServiceKeys.OSAGO, details)// 1) Сохраняем в SharedPreferences
            _uiState.update { it.copy(providerDetails = details) }// 2) Обновляем состояние экрана
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
        // Валидация
        val paymentDay = _uiState.value.paymentDay.toIntOrNull()
        val periodMonths = _uiState.value.periodMonths.toIntOrNull()
        val priceTariff = _uiState.value.providerDetails.tariff.toDoubleOrNull()
        val account = _uiState.value.providerDetails.accountNumber
        if (paymentDay == null || periodMonths == null || priceTariff == null) {
            _uiState.update { it.copy(error = ValidationError.InvalidInput) }
            return
        }
        // 👇 ДОБАВИТЬ: парсим дату
        val startDate = parseStartDate(_uiState.value.customDate)

        // 1️⃣ Домен - ЧТО рассчитать
        val data = osago.collectOsagoData(
            paymentDay = paymentDay,
            periodMonths = periodMonths,
            startDate = startDate,
            priceTariff = priceTariff,
            accountNumber = account
        )
        viewModelScope.launch {
            osagoRepository.saveOsagoPayment(data)
            _uiState.update {
                accountPrefs.savePaymentDay(SERVICE_KEY, paymentDay.toString())
                accountPrefs.savePeriodMonths(SERVICE_KEY, periodMonths.toString())
                accountPrefs.saveTariff(SERVICE_KEY, priceTariff.toString())
                it.copy(result = data, error = null)
            }
        }
    }
}
