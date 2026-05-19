package com.github.misham72.communalpayments.presentation.screen.screens.mts

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PaymentDateCalculatorImpl
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.MTSRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.MTS

class MTSViewModelFactory(context: Context) : ViewModelProvider.Factory {
    val calculator: PaymentDateCalculatorImpl = PaymentDateCalculatorImpl()
    private val mts = MTS(calculator)
    private val fileManager = FileManager(context)
    private val mtsRepository = MTSRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MTSViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral") return MTSViewModel(mts, mtsRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
