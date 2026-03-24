package com.github.misham72.communalpayments.presentation.screen.screens.electricity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.domain.repository.ElectricityRepository
import com.github.misham72.communalpayments.domain.userclasses.Electricity
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ElectricityViewModel(
    private val electricity: Electricity, //✅ Зависимости приходят через конструктор (внедрение зависимостей)
    private val electricityRepository: ElectricityRepository,  //✅ Зависимости приходят через конструктор (внедрение зависимостей)
    private val accountPrefs: AccountPreferences   // новая зависимость
) : ViewModel() {  //✅ Класс наследуется от ViewModel() — будет жить при поворотах
    companion object {
        private const val SERVICE_KEY = ServiceKeys.ELECTRICITY
    }

    /**Принимает пользовательский ввод (текущие показания, прошлые показания, тариф) и временно хранит их в UiState.*/
    data class UiState( //✅ UiState как единый источник правды для экрана
        val currentReading: String = "", val previousReading: String = "", val tariff: String = "", val accountNumber: String = "",
        val customServiceName: String = "", val showAccountDialog: Boolean = false,   // флаг для диалога
        val customDate: String = "", // дата, сохранённая в SharedPreferences
        val result: Electricity.ElectricityData? = null,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())  //✅ MutableStateFlow для изменяемого состояния
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()  //✅ StateFlow для неизменяемого публичного доступа

    //////////////////////////
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

    ////////////////////////////
    fun onCurrentReadingChange(value: String) {
        _uiState.update { it.copy(currentReading = value) }
    }

    fun onPreviousReadingChange(value: String) {
        _uiState.update { it.copy(previousReading = value) }
    }

    fun onTariffChange(value: String) {
        _uiState.update { it.copy(tariff = value) }
    }

    // ... говорит Domain ЧТО рассчитать
    // ... говорит Data КАК сохранить
    // ... говорит Screen что показать
    fun onCalculateClick() {// 🎯 Получает команды от Screen
        /** По команде (onCalculateClick) запускает цепочку действий:
        валидирует данные, вызывает доменную логику (electricity.collectElectricityData),
        форматирует результат и сохраняет его через meterPaymentStorage.*/
        val current = _uiState.value.currentReading.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val previous = _uiState.value.previousReading.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val tariff = _uiState.value.tariff.toDoubleOrNull() //Преобразует строки из полей ввода в числа (или null, если введена ерунда).
        val account = _uiState.value.accountNumber
        //Если хоть одно поле пустое или содержит не число — показывает ошибку и останавливается.

        if (current == null || previous == null || tariff == null) {
            _uiState.update { it.copy(errorMessage = "invalid input") }
            return
        }
        // ✅ 1. Бизнес-логика (без dateTime - это задача Repository)
        val data = electricity.collectElectricityData(
            current, previous, tariff, accountNumber = account
        )
        viewModelScope.launch {
            // ✅ 2. Сохранение (Repository сам добавит дату и отформатирует)
            electricityRepository.saveElectricityPayment(data)

            // ✅ 3. Обновление UI
            _uiState.update { it.copy(result = data, errorMessage = null) }

        }
    }
}
