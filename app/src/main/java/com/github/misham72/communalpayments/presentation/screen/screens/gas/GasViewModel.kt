package com.github.misham72.communalpayments.presentation.screen.screens.gas

import androidx.lifecycle.ViewModel
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.domain.repository.GasRepository
import com.github.misham72.communalpayments.domain.userclasses.Gas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GasViewModeL(
    private val gas: Gas,
    private val gasRepository: GasRepository,
    private val accountPrefs: AccountPreferences
) : ViewModel() {

    companion object {
        private const val SERVICE_KEY = "gas"
    }

    data class UiState(
        val currentReading: String = "", val previousReading: String = "", val tariff: String = "", val accountNumber: String = "",
        // 🔸 ДОБАВИТЬ НОВОЕ ПОЛЕ
        val customServiceName: String = "", val showAccountDialog: Boolean = false,   // флаг для диалога
        val result: Gas.GasData? = null, val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())  //✅ MutableStateFlow для изменяемого состояния
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()  //✅ StateFlow для неизменяемого публичного доступа

    init {
        val savedNumber = accountPrefs.getAccount(SERVICE_KEY)
        // 🔸 ЗАГРУЗИТЬ СОХРАНЁННОЕ НАЗВАНИЕ
        val savedName = accountPrefs.getCustomName(SERVICE_KEY)
        _uiState.update { it.copy(accountNumber = savedNumber, customServiceName = savedName) }
    }

    fun openAccountDialog() {
        _uiState.update { it.copy(showAccountDialog = true) }
    }

    fun closeAccountDialog() {
        _uiState.update { it.copy(showAccountDialog = false) }
    }


    // 🔸 ЗАМЕНИТЬ updateAccountNumber на updateAccountData (сохраняет и номер, и название)
    fun updateAccountData(newNumber: String, newName: String) {
        _uiState.update { it.copy(accountNumber = newNumber, customServiceName = newName) }
        accountPrefs.saveAccount(SERVICE_KEY, newNumber)
        accountPrefs.saveCustomName(SERVICE_KEY, newName)
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
        /** По команде (onCalculateClick) запускает цепочку действий:
        валидирует данные, вызывает доменную логику (electricity.collectElectricityData),
        форматирует результат и сохраняет его через meterPaymentStorage.*/
        val current = _uiState.value.currentReading.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val previous = _uiState.value.previousReading.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val tariff = _uiState.value.tariff.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val account = _uiState.value.accountNumber
        //Если хоть одно поле пустое или содержит не число — показывает ошибку и останавливается.

        if (current == null || previous == null || tariff == null) {
            _uiState.update { it.copy(errorMessage = "Invalid input") }
            return
        }
        // ✅ 1. Бизнес-логика (без dateTime - это задача Repository)
        val data = gas.collectGasData(
            current, previous, tariff, accountNumber = account
        )

        // ✅ 2. Сохранение (Repository сам добавит дату и отформатирует) gas(data)
        gasRepository.saveGasPayment(data)

        // ✅ 3. Обновление UI
        _uiState.update { it.copy(result = data, errorMessage = null) }
    }
}