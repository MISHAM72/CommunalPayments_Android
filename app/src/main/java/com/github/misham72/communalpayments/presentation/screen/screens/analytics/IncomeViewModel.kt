package com.github.misham72.communalpayments.presentation.screen.screens.analytics

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
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.Year

data class IncomeUiState(
    val isLoading: Boolean = true,
    val summary: IncomeSummary? = null,
    val error: String? = null
)

class IncomeViewModel(
    private val incomeUseCase: IncomeUseCase
) : ViewModel() {

    private val _recordsBySource = MutableStateFlow<Map<String, List<IncomeRecord>>>(emptyMap())
    val recordsBySource: StateFlow<Map<String, List<IncomeRecord>>> = _recordsBySource.asStateFlow()

    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    fun loadIncome(year: Int = Year.now().value) {
        viewModelScope.launch {
            _uiState.value = IncomeUiState()
            try {
                val summary = incomeUseCase.getYearlyIncome(year)   // ← готовый IncomeSummary
                val records = incomeUseCase.getIncomes(year)
                _recordsBySource.value = records.groupBy { it.source }
                _uiState.value = IncomeUiState(isLoading = false, summary = summary)
            } catch (e: Exception) {
                _uiState.value = IncomeUiState(isLoading = false, error = e.localizedMessage ?: UiMessages.DOWNLOAD_ERROR)
            }
        }
    }

    fun addIncome(source: String, amount: Double, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            incomeUseCase.addIncome(Year.now().value, date, source, amount)
            loadIncome(Year.now().value)
        }
    }

    fun updateRecord(oldRecord: IncomeRecord, newRecord: IncomeRecord) {
        viewModelScope.launch {
            incomeUseCase.updateIncome(Year.now().value, oldRecord, newRecord)
            loadIncome()
        }
    }

    fun deleteRecord(record: IncomeRecord) {
        viewModelScope.launch {
            incomeUseCase.deleteIncome(Year.now().value, record)
            loadIncome()
        }
    }

    fun deleteAllRecordsBySource(source: String) {
        viewModelScope.launch {
            incomeUseCase.deleteAllBySource(Year.now().value, source)
            loadIncome()
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
                year = Year.now().value,
                record = record,
                bytes = bytes,
                fileName = fileName,
                mimeType = mimeType
            )
            loadIncome()
        }
    }

    fun removeAttachment(record: IncomeRecord, attachment: Attachment) {
        viewModelScope.launch {
            incomeUseCase.removeAttachment(Year.now().value, record, attachment)
            loadIncome()
        }
    }

    fun getAttachmentFile(path: String): File? {
        return incomeUseCase.getAttachment(path)
    }
}
