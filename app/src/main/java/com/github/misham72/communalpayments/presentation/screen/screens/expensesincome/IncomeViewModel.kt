package com.github.misham72.communalpayments.presentation.screen.screens.expensesincome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.model.Attachment
import com.github.misham72.communalpayments.domain.model.incomes.IncomeRecord
import com.github.misham72.communalpayments.domain.model.incomes.IncomeSummary
import com.github.misham72.communalpayments.domain.usecases.IncomeUseCase
import com.github.misham72.communalpayments.presentation.common.UiMessages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.Year

enum class IncomePeriod { Year, Month }

data class IncomeUiState(
    val isLoading: Boolean = true,
    val summary: IncomeSummary? = null,
    val error: String? = null,
    val period: IncomePeriod = IncomePeriod.Year,
    val selectedYear: Int = Year.now().value,
    val selectedMonth: Int = LocalDate.now().monthValue,
)

class IncomeViewModel(
    private val incomeUseCase: IncomeUseCase
) : ViewModel() {

    private val _recordsBySource = MutableStateFlow<Map<String, List<IncomeRecord>>>(emptyMap())
    val recordsBySource: StateFlow<Map<String, List<IncomeRecord>>> = _recordsBySource.asStateFlow()

    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    fun loadIncome(year: Int = Year.now().value) {
        _uiState.update { it.copy(selectedYear = year) }
        loadInternal()
    }

    fun setPeriod(period: IncomePeriod) {
        _uiState.update { it.copy(period = period) }
        loadInternal()
    }

    fun previousMonth() {
        _uiState.update { state ->
            val newMonth = if (state.selectedMonth == 1) 12 else state.selectedMonth - 1
            val newYear = if (state.selectedMonth == 1) state.selectedYear - 1 else state.selectedYear
            state.copy(selectedMonth = newMonth, selectedYear = newYear)
        }
        loadInternal()
    }

    fun nextMonth() {
        _uiState.update { state ->
            val newMonth = if (state.selectedMonth == 12) 1 else state.selectedMonth + 1
            val newYear = if (state.selectedMonth == 12) state.selectedYear + 1 else state.selectedYear
            state.copy(selectedMonth = newMonth, selectedYear = newYear)
        }
        loadInternal()
    }

    private fun loadInternal() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val state = _uiState.value
                val summary = if (state.period == IncomePeriod.Year) {
                    incomeUseCase.getYearlyIncome(state.selectedYear)
                } else {
                    incomeUseCase.getMonthlyIncome(state.selectedYear, state.selectedMonth)
                }
                val records = incomeUseCase.getIncomes(state.selectedYear)
                _recordsBySource.value = records.groupBy { it.source }
                _uiState.update {
                    it.copy(isLoading = false, summary = summary, error = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: UiMessages.DOWNLOAD_ERROR
                    )
                }
            }
        }
    }

    // --- Остальные методы ---

    fun addIncome(source: String, amount: Double, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            incomeUseCase.addIncome(_uiState.value.selectedYear, date, source, amount)
            loadInternal()
        }
    }

    fun updateRecord(oldRecord: IncomeRecord, newRecord: IncomeRecord) {
        viewModelScope.launch {
            incomeUseCase.updateIncome(_uiState.value.selectedYear, oldRecord, newRecord)
            loadInternal()
        }
    }

    fun deleteRecord(record: IncomeRecord) {
        viewModelScope.launch {
            incomeUseCase.deleteIncome(_uiState.value.selectedYear, record)
            loadInternal()
        }
    }

    fun deleteAllRecordsBySource(source: String) {
        viewModelScope.launch {
            incomeUseCase.deleteAllBySource(_uiState.value.selectedYear, source)
            loadInternal()
        }
    }

    fun attachAttachment(
        record: IncomeRecord,
        bytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        viewModelScope.launch {
            incomeUseCase.attachAttachment(
                year = _uiState.value.selectedYear,
                record = record,
                bytes = bytes,
                fileName = fileName,
                mimeType = mimeType
            )
            loadInternal()
        }
    }

    fun removeAttachment(record: IncomeRecord, attachment: Attachment) {
        viewModelScope.launch {
            incomeUseCase.removeAttachment(_uiState.value.selectedYear, record, attachment)
            loadInternal()
        }
    }

    fun getAttachmentFile(path: String): File? {
        return incomeUseCase.getAttachment(path)
    }
}
