package com.github.misham72.communalpayments.presentation.screen.screens.heating

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PeriodCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.data.repository.periodicRepository.ZONTRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.usecases.ZONT


class ZONTViewModelFactory(context: Context) : ViewModelProvider.Factory {
    val calculator: PeriodCalculatorImpl = PeriodCalculatorImpl()
    private val zont = ZONT(calculator)
    private val fileManager = FileManager(context)
    private val zontRepository = ZONTRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ZONTViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral") return ZONTViewModel(
                zont = zont,
                zontRepository = zontRepository,
                accountPrefs = accountPrefs,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
