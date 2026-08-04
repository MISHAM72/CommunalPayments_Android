package com.github.misham72.communalpayments.presentation.screen.screens.water

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.MeterCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.data.repository.MeterRepository.WaterRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.userclasses.Water

class WaterViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val meterCalculator = MeterCalculatorImpl()
    private val water = Water(meterCalculator)
    private val fileManager = FileManager(context)
    private val waterRepository = WaterRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral") return WaterViewModel(
                water = water,
                waterRepository = waterRepository,
                accountPrefs = accountPrefs,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
