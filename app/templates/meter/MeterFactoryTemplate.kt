package com.github.misham72.communalpayments.presentation.screen.screens.%

service_name%

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.repository.%

SERVICE_NAME%Repository
import com.github.misham72.communalpayments.domain.userclasses.%

SERVICE_NAME%

/**
 * ШАБЛОН для Factory счетчика
 *
 * КАК ИСПОЛЬЗОВАТЬ:
 * 1. Замените %SERVICE_NAME% на название сервиса (Water, Gas)
 * 2. Замените %service_name% на название в нижнем регистре (water, gas)
 * 3. Скопируйте в presentation/screen/screens/%service_name%/ как %SERVICE_NAME%ViewModelFactory.kt
 */
class %SERVICE_NAME%ViewModelFactory(
private val %service_name%: %SERVICE_NAME%,
private val %service_name%Repository: %SERVICE_NAME%Repository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(% SERVICE_NAME % ViewModel ::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return % SERVICE_NAME % ViewModel(% service_name %, %service_name%Repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
/////
package com.github.misham72.communalpayments.presentation.screen.screens.water

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.repository.WaterRepository
import com.github.misham72.communalpayments.domain.userclasses.Water

class WaterViewModelFactory(
    private val water: Water,
    private val waterRepository: WaterRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WaterViewModel(water, waterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
