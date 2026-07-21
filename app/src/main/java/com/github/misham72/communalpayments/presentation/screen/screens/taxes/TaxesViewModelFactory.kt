package com.github.misham72.communalpayments.presentation.screen.screens.taxes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PaymentDateCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.data.repository.PeriodicRepository.TaxesRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.userclasses.Taxes

class TaxesViewModelFactory(context: Context) : ViewModelProvider.Factory {
    val calculator: PaymentDateCalculatorImpl = PaymentDateCalculatorImpl()
    private val taxes = Taxes(calculator)
    private val fileManager = FileManager(context)
    private val taxesRepository = TaxesRepositoryImpl(context, fileManager)

    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaxesViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral") return TaxesViewModel(
                taxes = taxes,
                taxesRepository = taxesRepository,
                accountPrefs = accountPrefs,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
