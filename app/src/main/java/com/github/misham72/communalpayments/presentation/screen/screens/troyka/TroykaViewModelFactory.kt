package com.github.misham72.communalpayments.presentation.screen.screens.troyka

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PeriodCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.data.repository.PeriodicRepository.TroykaRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.userclasses.Troyka

class TroykaViewModelFactory(context: Context) : ViewModelProvider.Factory {
    val calculator: PeriodCalculatorImpl = PeriodCalculatorImpl()
    private val troyka = Troyka(calculator)
    private val fileManager = FileManager(context)
    private val troykaRepository = TroykaRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TroykaViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral") return TroykaViewModel(
                troyka = troyka,
                troykaRepository = troykaRepository,
                accountPrefs = accountPrefs,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
