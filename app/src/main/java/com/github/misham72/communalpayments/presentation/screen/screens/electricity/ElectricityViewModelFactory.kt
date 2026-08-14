package com.github.misham72.communalpayments.presentation.screen.screens.electricity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.repository.meterrepository.ElectricityRepositoryImpl
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.data.repository.settings.UserSettingsRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.MeterDataCollector

class ElectricityViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val fileManager = FileManager(context)
    private val electricityRepository = ElectricityRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)
    private val settingsRepository: UserSettingsRepository = UserSettingsRepositoryImpl(accountPrefs)
    private val meterDataCollector = MeterDataCollector(electricityRepository, settingsRepository)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectricityViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return ElectricityViewModel(
                meterDataCollector = meterDataCollector,
                settingsRepository = settingsRepository,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
