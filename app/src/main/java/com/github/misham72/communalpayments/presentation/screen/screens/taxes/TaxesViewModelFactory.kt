package com.github.misham72.communalpayments.presentation.screen.screens.taxes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PaymentDateCalculatorImpl
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.TaxesRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.Taxes

class TaxesViewModelFactory(context: Context) : ViewModelProvider.Factory {
    val calculator = PaymentDateCalculatorImpl()
    private val taxes = Taxes(calculator)
    private val fileManager = FileManager(context)
    private val taxesRepository = TaxesRepositoryImpl(context, fileManager)

    private val accountPrefs = AccountPreferences(context.applicationContext)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaxesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return TaxesViewModel(taxes, taxesRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
