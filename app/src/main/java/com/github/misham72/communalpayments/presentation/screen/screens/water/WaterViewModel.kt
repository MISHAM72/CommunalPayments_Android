package com.github.misham72.communalpayments.presentation.screen.screens.water

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.domain.repository.WaterRepository
import com.github.misham72.communalpayments.domain.userclasses.Water
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import com.github.misham72.communalpayments.presentation.screen.screens.electricity.ElectricityViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class WaterViewModel(
    private val water: Water,
    private val waterRepository: WaterRepository,
    private val accountPrefs: AccountPreferences
) : ViewModel() {
    companion object {
        private const val SERVICE_KEY = ServiceKeys.WATER
    }

    data class UiState( //✅ UiState как единый источник правды для экрана
        val currentReading: String = "",
        val previousReading: String = "",
        val tariff: String = "",
        val accountNumber: String = "",
        val customDate: String = "",
        val customServiceName: String = "",
        val showAccountDialog: Boolean = false,   // флаг для диалога
        val result: Water.WaterData? = null,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())  //✅ MutableStateFlow для изменяемого состояния
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()  //✅ StateFlow для неизменяемого публичного доступа

    init {
        val savedNumber = accountPrefs.getAccount(SERVICE_KEY)
        // 🔸 ЗАГРУЗИТЬ СОХРАНЁННОЕ НАЗВАНИЕ
        val savedName = accountPrefs.getCustomName(SERVICE_KEY)
        val saveDate = accountPrefs.getCustomDate(SERVICE_KEY)

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
        accountPrefs.saveCustomName(SERVICE_KEY, newName)
        accountPrefs.saveCustomDate(SERVICE_KEY, newDate)

    }

    fun onCurrentReadingChange(value: String) {
        _uiState.update { it.copy(currentReading = value) }
    }

    fun onPreviousReadingChange(value: String) {
        _uiState.update { it.copy(previousReading = value) }
    }

    fun onTariffChange(value: String) {
        _uiState.update { it.copy(tariff = value) }
    }

    fun onCalculateClick() {
        val current = _uiState.value.currentReading.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val previous = _uiState.value.previousReading.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val tariff = _uiState.value.tariff.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val account = _uiState.value.accountNumber

        if (current == null || previous == null || tariff == null) {
            _uiState.update { it.copy(errorMessage = "Invalid input") }
            return
        }

        val data = water.collectWaterData(
            current, previous, tariff, accountNumber = account
        )
        viewModelScope.launch {
            // ✅ 2. Сохранение (Repository сам добавит дату и отформатирует)
            waterRepository.saveWaterPayment(data)

            // ✅ 3. Обновление UI
            _uiState.update { it.copy(result = data, errorMessage = null) }
        }
    }
}
