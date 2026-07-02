package com.github.misham72.communalpayments.presentation.screen.screens.%

service_name%

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.repository.%

SERVICE_NAME%Repository
import com.github.misham72.communalpayments.domain.userclasses.%

SERVICE_NAME%


class %SERVICE_NAME%ViewModelFactory(
private val %service_name%: %SERVICE_NAME%,
private val %service_name%Repository: %SERVICE_NAME%Repository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(% SERVICE_NAME % ViewModel ::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return % SERVICE_NAME % ViewModel(% service_name %, %service_name%Repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
