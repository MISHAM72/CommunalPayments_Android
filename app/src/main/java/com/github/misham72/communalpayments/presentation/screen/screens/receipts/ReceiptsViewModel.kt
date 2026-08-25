package com.github.misham72.communalpayments.presentation.screen.screens.receipts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.model.Receipt
import com.github.misham72.communalpayments.domain.usecases.GetReceiptsUseCase
import com.github.misham72.communalpayments.domain.usecases.DeleteReceiptUseCase
import com.github.misham72.communalpayments.domain.usecases.SaveReceiptUseCase
import com.github.misham72.communalpayments.presentation.common.UiMessages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream

class ReceiptsViewModel(
    private val getReceiptsUseCase: GetReceiptsUseCase,
    private val deleteReceiptUseCase: DeleteReceiptUseCase,
    private val saveReceiptUseCase: SaveReceiptUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ReceiptsUiState>(ReceiptsUiState.Loading)
    val uiState: StateFlow<ReceiptsUiState> = _uiState.asStateFlow()

    fun loadReceipts(serviceKey: String) {
        viewModelScope.launch {
            _uiState.value = ReceiptsUiState.Loading
            try {
                val receipts = getReceiptsUseCase(serviceKey)
                _uiState.value = ReceiptsUiState.Success(receipts)
            } catch (e: Exception) {
                _uiState.value = ReceiptsUiState.Error(e.message ?: UiMessages.DOWNLOAD_ERROR)
            }
        }
    }

    fun deleteReceipt(id: String, serviceKey: String) {
        viewModelScope.launch {
            deleteReceiptUseCase(id)
            loadReceipts(serviceKey)
        }
    }


    fun addReceipt(inputStream: InputStream, fileName: String, serviceKey: String) {
        viewModelScope.launch {
            saveReceiptUseCase(serviceKey, inputStream, fileName)
            loadReceipts(serviceKey)
        }
    }
}

sealed class ReceiptsUiState {
    object Loading : ReceiptsUiState()
    data class Success(val receipts: List<Receipt>) : ReceiptsUiState()
    data class Error(val message: String) : ReceiptsUiState()
}
