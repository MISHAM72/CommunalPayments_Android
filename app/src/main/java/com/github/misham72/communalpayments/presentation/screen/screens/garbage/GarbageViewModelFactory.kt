package com.github.misham72.communalpayments.presentation.screen.screens.garbage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PeriodCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.PeriodicRepository.GarbageRepositoryImpl
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.userclasses.Garbage

class GarbageViewModelFactory(context: Context) : ViewModelProvider.Factory {

    val calculator: PeriodCalculatorImpl = PeriodCalculatorImpl()
    private val garbage = Garbage(calculator)
    private val fileManager = FileManager(context)
    private val garbageRepository = GarbageRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GarbageViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return GarbageViewModel(
                garbage = garbage,
                garbageRepository = garbageRepository,
                accountPrefs = accountPrefs,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
