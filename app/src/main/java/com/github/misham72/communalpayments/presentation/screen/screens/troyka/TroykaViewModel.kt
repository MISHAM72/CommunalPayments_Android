package com.github.misham72.communalpayments.presentation.screen.screens.troyka


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.domain.repository.TroykaRepository
import com.github.misham72.communalpayments.domain.userclasses.Troyka
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.screen.screens.garbage.GarbageViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TroykaViewModel(
    private val troyka: Troyka,                   // Домен
    private val troykaRepository: TroykaRepository, private val accountPrefs: AccountPreferences
) : ViewModel() {

    companion object {
        private const val SERVICE_KEY = ServiceKeys.TROYKA
    }

    // 1️⃣ СОСТОЯНИЕ ЭКРАНА (что храним)
    data class UiState(
        val paymentDay: String = "",      // день платежа
        val periodMonths: String = "",    // период в месяцах
        val priceTariff: String = "", val accountNumber: String = "",
        // 🔸 ДОБАВИТЬ НОВОЕ ПОЛЕ
        val customServiceName: String = "",
        val customDate: String = "",
        val showAccountDialog: Boolean = false,   // флаг для диалога
        val result: Troyka.TroykaData? = null, val errorMessage: String? = null
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

    // 3️⃣ ГЛАВНАЯ ЛОГИКА (как в Electricity)
    fun onCalculateClick() {
        // Валидация
        val paymentDay = _uiState.value.paymentDay.toIntOrNull()
        val periodMonths = _uiState.value.periodMonths.toIntOrNull()
        val priceTariff = _uiState.value.priceTariff.toDoubleOrNull()
        val account = _uiState.value.accountNumber

        if (paymentDay == null || periodMonths == null || priceTariff == null) {
            _uiState.update { it.copy(errorMessage = "Invalid input") }
            return
        }

        // 1️⃣ Домен - ЧТО рассчитать
        val data = troyka.collectTroykaData(
            paymentDay, periodMonths, priceTariff, accountNumber = account
        )
        viewModelScope.launch {
            // 2️⃣ Data - КАК сохранить
            troykaRepository.saveTroykaPayment(data)

            // 3️⃣ Обновляем UI
            _uiState.update { it.copy(result = data, errorMessage = null) }
        }
    }
}