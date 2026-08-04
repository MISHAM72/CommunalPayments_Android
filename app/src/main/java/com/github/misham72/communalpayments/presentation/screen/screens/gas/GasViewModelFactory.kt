package com.github.misham72.communalpayments.presentation.screen.screens.gas

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.MeterCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.MeterRepository.GasRepositoryImpl
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.userclasses.Gas

class GasViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val meterCalculator = MeterCalculatorImpl()
    private val gas = Gas(meterCalculator)
    private val fileManager = FileManager(context)
    private val gasRepository = GasRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GasViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return GasViewModel(gas, gasRepository, accountPrefs = accountPrefs, repository = providerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
