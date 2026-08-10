package com.github.misham72.communalpayments.presentation.screen.screens.troyka

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PeriodCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.periodicRepository.PeriodicRepositoryImpl
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.usecases.PeriodicDataCollector

class TroykaViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val fileManager = FileManager(context)
    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)
    private val calculator: PeriodCalculatorImpl = PeriodCalculatorImpl()
    private val periodicRepository = PeriodicRepositoryImpl(context, fileManager)
    private val periodicDataCollector = PeriodicDataCollector(periodicRepository, accountPrefs, calculator)


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TroykaViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return TroykaViewModel(
                periodicDataCollector = periodicDataCollector,
                accountPrefs = accountPrefs,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
