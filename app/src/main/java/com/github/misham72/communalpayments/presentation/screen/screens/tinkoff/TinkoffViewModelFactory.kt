package com.github.misham72.communalpayments.presentation.screen.screens.tinkoff

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PeriodCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.data.repository.PeriodicRepository.TinkoffRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.usecases.Tinkoff

class TinkoffViewModelFactory(context: Context) : ViewModelProvider.Factory {
    val calculator: PeriodCalculatorImpl = PeriodCalculatorImpl()

    private val tinkoff = Tinkoff(calculator)
    private val fileManager = FileManager(context)
    private val tinkoffRepository = TinkoffRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TinkoffViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return TinkoffViewModel(
                tinkoff = tinkoff,
                tinkoffRepository = tinkoffRepository,
                accountPrefs = accountPrefs,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
