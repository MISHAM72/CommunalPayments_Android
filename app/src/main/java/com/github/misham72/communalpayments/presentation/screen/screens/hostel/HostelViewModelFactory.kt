package com.github.misham72.communalpayments.presentation.screen.screens.hostel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PeriodCalculatorImpl
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.data.local.file.FileManager
import com.github.misham72.communalpayments.data.repository.PeriodicRepository.HostelRepositoryImpl
import com.github.misham72.communalpayments.data.repository.provider.ProviderRepositoryImpl
import com.github.misham72.communalpayments.domain.repository.IProviderRepository
import com.github.misham72.communalpayments.domain.usecases.Hostel

class HostelViewModelFactory(context: Context) : ViewModelProvider.Factory {

    val calculator: PeriodCalculatorImpl = PeriodCalculatorImpl()
    private val hostel = Hostel(calculator)
    private val fileManager = FileManager(context)
    private val hostelRepository = HostelRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)
    private val providerRepository: IProviderRepository = ProviderRepositoryImpl(accountPrefs)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HostelViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return HostelViewModel(
                hostel = hostel,
                hostelRepository = hostelRepository,
                accountPrefs = accountPrefs,
                repository = providerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
