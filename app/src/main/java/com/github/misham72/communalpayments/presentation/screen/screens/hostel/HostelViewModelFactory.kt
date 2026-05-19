package com.github.misham72.communalpayments.presentation.screen.screens.hostel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PaymentDateCalculatorImpl
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.HostelRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.Hostel

class HostelViewModelFactory(context: Context) : ViewModelProvider.Factory {

    val calculator: PaymentDateCalculatorImpl = PaymentDateCalculatorImpl()
    private val hostel = Hostel(calculator)
    private val fileManager = FileManager(context)
    private val hostelRepository = HostelRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HostelViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return HostelViewModel(hostel, hostelRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
