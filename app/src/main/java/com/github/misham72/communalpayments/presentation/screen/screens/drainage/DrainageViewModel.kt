package com.github.misham72.communalpayments.presentation.screen.screens.drainage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.parser.HistoryParser
import com.github.misham72.communalpayments.data.repository.drainagerepository.DrainageRepositoryImpl
import com.github.misham72.communalpayments.domain.constants.ServiceKeys
import com.github.misham72.communalpayments.domain.model.ProviderDetails
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.PdfHistoryUseCase
import com.github.misham72.communalpayments.domain.usecases.TextHistoryUseCase
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DrainageViewModel(
    private val settingsRepository: UserSettingsRepository,
    private val repository: IProviderRepository,
    private val drainageRepository: DrainageRepositoryImpl,
    private val fileManager: FileManager,
    private val textHistoryUseCase: TextHistoryUseCase,
    private val pdfHistoryUseCase: PdfHistoryUseCase,
    private val gson: Gson
) : ViewModel() {

    companion object {
        const val SERVICE_KEY = ServiceKeys.DRAINAGE
        private const val KEY_HAS_HOT_WATER = ServiceKeys.KEY_DRAINAGE_HAS_HOT_WATER
    }

    data class UiState(
        val coldUsage: Double = 0.0,
        val hotUsage: Double = 0.0,
        val totalUsage: Double = 0.0,
        val hasHotWater: Boolean = true,
        val hasColdData: Boolean = false,
        val hasHotData: Boolean = false,
        val providerDetails: ProviderDetails = ProviderDetails(),
        val showAccountDialog: Boolean = false,
        val customDate: String = "",
        val lastResult: DrainageResult? = null,
    )

    data class DrainageResult(
        val coldUsage: Double,
        val hotUsage: Double,
        val totalUsage: Double,
        val tariff: Double,
        val payment: Double,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val detailsDeferred = async { repository.loadProviderDetails(ServiceKeys.DRAINAGE) }
            val savedTariff = settingsRepository.getTariff(SERVICE_KEY) ?: ""
            val savedDate = settingsRepository.getCustomDate(SERVICE_KEY)
            val savedHasHotWater = settingsRepository.getLastReading(KEY_HAS_HOT_WATER)
                ?.toBooleanStrictOrNull() ?: true
            // Читаем последние расходы ХВС и ГВС (сохранены как простые числа)
            val coldUsage = HistoryParser.extractLatestConsumption(
                fileManager.readHistory(ServiceKeys.COLDWATER)
            ) ?: 0.0

            val hotUsage = HistoryParser.extractLatestConsumption(
                fileManager.readHistory(ServiceKeys.HOTWATER)
            ) ?: 0.0

            val details = detailsDeferred.await()
            val savedJson = settingsRepository.getLastResult(SERVICE_KEY)
            val lastResult = savedJson?.let { gson.fromJson(it, DrainageResult::class.java) }
            _uiState.update {
                it.copy(
                    coldUsage = coldUsage,
                    hotUsage = hotUsage,
                    hasHotWater = savedHasHotWater,
                    hasColdData = coldUsage > 0.0,
                    hasHotData = hotUsage > 0.0,
                    lastResult = lastResult,
                    providerDetails = details.copy(tariff = savedTariff.ifBlank { details.tariff }),
                    customDate = savedDate,
                )
            }
            recalcTotal()
        }
    }

    private fun recalcTotal() {
        _uiState.update { state ->
            val total = if (state.hasHotWater) state.coldUsage + state.hotUsage else state.coldUsage
            state.copy(totalUsage = total)
        }
    }

    fun toggleHasHotWater(value: Boolean) {
        _uiState.update { it.copy(hasHotWater = value) }
        recalcTotal()
        viewModelScope.launch {
            settingsRepository.saveLastReading(KEY_HAS_HOT_WATER, value.toString())
        }
    }

    fun onTariffChange(value: String) {
        _uiState.update {
            it.copy(providerDetails = it.providerDetails.copy(tariff = value))
        }
    }

    fun updateCustomDate(date: String) {
        _uiState.update { it.copy(customDate = date) }
        viewModelScope.launch { settingsRepository.saveCustomDate(SERVICE_KEY, date) }
    }

    fun openAccountDialog() {
        _uiState.update { it.copy(showAccountDialog = true) }
    }

    fun closeAccountDialog() {
        _uiState.update { it.copy(showAccountDialog = false) }
    }

    fun saveProviderDetails(details: ProviderDetails) {
        viewModelScope.launch {
            repository.saveProviderDetails(ServiceKeys.DRAINAGE, details)
            _uiState.update { it.copy(providerDetails = details) }
        }
    }

    fun onShareClick(context: Context) {
        viewModelScope.launch { textHistoryUseCase.shareSingleHistory(context, SERVICE_KEY) }
    }

    fun onPdfExport(context: Context) {
        viewModelScope.launch { pdfHistoryUseCase.exportSingleHistoryPdf(context, SERVICE_KEY) }
    }

    fun onCalculateClick() {
        val state = _uiState.value
        val tariff = state.providerDetails.tariff.toDoubleOrNull() ?: return
        val total = if (state.hasHotWater) state.coldUsage + state.hotUsage else state.coldUsage
        val payment = total * tariff

        viewModelScope.launch {
            drainageRepository.save(
                accountNumber = state.providerDetails.accountNumber,
                coldUsage = state.coldUsage,
                hotUsage = if (state.hasHotWater) state.hotUsage else 0.0,
                totalUsage = total,
                tariff = tariff,
                payment = payment,
                nextPaymentDate = state.customDate,
                isHistory = true
            )
            settingsRepository.saveTariff(SERVICE_KEY, tariff.toString())
            val result = DrainageResult(
                coldUsage = state.coldUsage,
                hotUsage = if (state.hasHotWater) state.hotUsage else 0.0,
                totalUsage = total,
                tariff = tariff,
                payment = payment,
            )
            settingsRepository.saveLastResult(SERVICE_KEY, gson.toJson(result))
            _uiState.update {
                it.copy(
                    totalUsage = total,
                    lastResult = DrainageResult(
                        coldUsage = it.coldUsage,
                        hotUsage = if (it.hasHotWater) it.hotUsage else 0.0,
                        totalUsage = total,
                        tariff = tariff,
                        payment = payment,
                    ),
                )
            }
        }
    }
}
