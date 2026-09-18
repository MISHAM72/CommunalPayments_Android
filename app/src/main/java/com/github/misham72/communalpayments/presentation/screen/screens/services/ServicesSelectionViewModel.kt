package com.github.misham72.communalpayments.presentation.screen.screens.services

import androidx.lifecycle.ViewModel
import com.github.misham72.communalpayments.domain.model.SelectedServices
import com.github.misham72.communalpayments.domain.repository.SelectedServicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ServicesSelectionViewModel(
    private val repository: SelectedServicesRepository,
    meterItems: List<ServiceItem>,
    periodicItems: List<ServiceItem>,
    groupTitles: Pair<String, String>,
) : ViewModel() {

    private val _state = MutableStateFlow(
        ServicesSelectionState(
            groups = listOf(
                ServiceGroup(groupTitles.first, meterItems),
                ServiceGroup(groupTitles.second, periodicItems),
            ),
            selectedKeys = repository.getSelected().keys,
        )
    )
    val state: StateFlow<ServicesSelectionState> = _state.asStateFlow()

    fun toggle(key: String) {
        _state.update { current ->
            val newKeys = if (key in current.selectedKeys) {
                current.selectedKeys - key
            } else {
                current.selectedKeys + key
            }
            current.copy(selectedKeys = newKeys)
        }
    }

    fun save() {
        repository.saveSelected(SelectedServices(_state.value.selectedKeys))
        _state.update { it.copy(isSaved = true) }
    }
}
