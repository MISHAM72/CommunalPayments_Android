package com.github.misham72.communalpayments.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.misham72.communalpayments.domain.userclasses.ExportBackupUseCase
import com.github.misham72.communalpayments.domain.userclasses.ImportBackupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream

class BackupViewModel(
    private val exportUseCase: ExportBackupUseCase,
    private val importUseCase: ImportBackupUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BackupState())
    val state: StateFlow<BackupState> = _state

    fun exportBackup(outputStream: OutputStream) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val success = exportUseCase(outputStream)
            _state.value = _state.value.copy(
                isLoading = false,
                isExportDone = success,
                error = if (!success) "Ошибка при создании копии" else null
            )
        }
    }

    fun importBackup(inputStream: InputStream) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val success = importUseCase(inputStream)
            _state.value = _state.value.copy(
                isLoading = false,
                isImportDone = success,
                error = if (!success) "Ошибка при восстановлении" else null
            )
        }
    }

    fun resetState() {
        _state.value = BackupState()
    }

    data class BackupState(
        val isLoading: Boolean = false,
        val isExportDone: Boolean = false,
        val isImportDone: Boolean = false,
        val error: String? = null
    )
}
