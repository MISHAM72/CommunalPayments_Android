package com.github.misham72.communalpayments.presentation.screen.screens.tinkoff

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PaymentDateCalculatorImpl
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.TinkoffRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.Tinkoff

class TinkoffViewModelFactory(context: Context) : ViewModelProvider.Factory {
    val calculator: PaymentDateCalculatorImpl = PaymentDateCalculatorImpl()

    private val tinkoff = Tinkoff(calculator)
    private val fileManager = FileManager(context)
    private val tinkoffRepository = TinkoffRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TinkoffViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral", "UNCHECKED_CAST") return TinkoffViewModel(tinkoff, tinkoffRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
