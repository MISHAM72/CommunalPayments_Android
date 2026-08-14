package com.github.misham72.communalpayments.presentation.screen.screens.tinkoff

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PeriodCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.periodrepository.PeriodicRepositoryImpl
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.data.repository.settings.UserSettingsRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.repository.UserSettingsRepository
import com.github.misham72.communalpayments.domain.usecases.PeriodicDataCollector

class TinkoffViewModelFactory(context: Context) : ViewModelProvider.Factory {


    private val fileManager = FileManager(context)

    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)
    private val calculator: PeriodCalculatorImpl = PeriodCalculatorImpl()
    private val periodicRepository = PeriodicRepositoryImpl(context, fileManager)
    private val settingsRepository: UserSettingsRepository = UserSettingsRepositoryImpl(accountPrefs)
    private val periodicDataCollector = PeriodicDataCollector(periodicRepository, settingsRepository, calculator)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TinkoffViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return TinkoffViewModel(
                periodicDataCollector = periodicDataCollector,
                settingsRepository = settingsRepository,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
