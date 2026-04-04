package com.github.misham72.communalpayments.presentation.screen.screens.mts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.domain.model.ValidationError
import com.github.misham72.communalpayments.domain.repository.MTSRepository
import com.github.misham72.communalpayments.domain.userclasses.MTS
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
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
    private val mtsRepository: MTSRepository, private val accountPrefs: AccountPreferences
) : ViewModel() {

    companion object {
        private const val SERVICE_KEY = ServiceKeys.MTS
    }

    // 1️⃣ СОСТОЯНИЕ ЭКРАНА (что храним)
    data class UiState(
        val paymentDay: String = "",      // день платежа
        val periodMonths: String = "",    // период в месяцах
        val priceTariff: String = "", val accountNumber: String = "",
        val customDate: String = "",
        val customServiceName: String = "", val showAccountDialog: Boolean = false,   // флаг для диалога
        val result: MTS.MTSData? = null,
        val error: ValidationError? = null   // вместо errorMessage: String?

    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val savedNumber = accountPrefs.getAccount(SERVICE_KEY)
        val saveDate = accountPrefs.getCustomDate(SERVICE_KEY)
        val savedName = accountPrefs.getCustomName(SERVICE_KEY)
        _uiState.update { it.copy(accountNumber = savedNumber, customServiceName = savedName, customDate = saveDate) }
    }

    fun openAccountDialog() {
        _uiState.update { it.copy(showAccountDialog = true) }
    }

    fun closeAccountDialog() {
        _uiState.update { it.copy(showAccountDialog = false) }
    }


    // 🔸 ЗАМЕНИТЬ updateAccountNumber на updateAccountData (сохраняет и номер, и название)
    fun updateAccountData(newNumber: String, newName: String, newDate: String) {
        _uiState.update { it.copy(accountNumber = newNumber, customServiceName = newName, customDate = newDate) }
        accountPrefs.saveAccount(SERVICE_KEY, newNumber)
        accountPrefs.saveCustomDate(SERVICE_KEY, newDate)
        accountPrefs.saveCustomName(SERVICE_KEY, newName)
    }

    // 2️⃣ ПОЛУЧАЕМ ВВОД ОТ ПОЛЬЗОВАТЕЛЯ
    fun onPaymentDayChange(value: String) {
        _uiState.update { it.copy(paymentDay = value) }
    }

    fun onPeriodMonthsChange(value: String) {
        _uiState.update { it.copy(periodMonths = value) }
    }

    fun onPriceTariffChange(value: String) {
        _uiState.update { it.copy(priceTariff = value) }
    }

    private fun parseStartDate(dateString: String): Date {
        return try {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (_: Exception) {
            Date() // при ошибке используем текущую дату
        }
    }// 3️⃣ ГЛАВНАЯ ЛОГИКА (как в Electricity)

    fun onCalculateClick() {
        // Валидация
        val paymentDay = _uiState.value.paymentDay.toIntOrNull()
        val periodMonths = _uiState.value.periodMonths.toIntOrNull()
        val priceTariff = _uiState.value.priceTariff.toDoubleOrNull()
        val account = _uiState.value.accountNumber
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
            // 2️⃣ Data - КАК сохранить
            mtsRepository.saveMTSPayment(data)

            // 3️⃣ Обновляем UI
            _uiState.update { it.copy(result = data, error = null) }
        }
    }
}