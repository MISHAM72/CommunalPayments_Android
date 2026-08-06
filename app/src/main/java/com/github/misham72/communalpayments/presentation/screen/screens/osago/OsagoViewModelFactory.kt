package com.github.misham72.communalpayments.presentation.screen.screens.osago

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PeriodCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.PeriodicRepository.OsagoRepositoryImpl
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.usecases.Osago

class OsagoViewModelFactory(context: Context) : ViewModelProvider.Factory {
    val calculator: PeriodCalculatorImpl = PeriodCalculatorImpl()
    private val osago = Osago(calculator)
    private val fileManager = FileManager(context)
    private val osagoRepository = OsagoRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OsagoViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return OsagoViewModel(
                osago = osago,
                osagoRepository = osagoRepository,
                accountPrefs = accountPrefs,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
