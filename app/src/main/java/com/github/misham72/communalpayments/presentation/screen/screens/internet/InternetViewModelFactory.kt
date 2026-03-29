package com.github.misham72.communalpayments.presentation.screen.screens.internet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.calculators.PaymentDateCalculatorImpl
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.InternetRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.Internet


class InternetViewModelFactory(context: Context) : ViewModelProvider.Factory {
    val calculator = PaymentDateCalculatorImpl()
    private val internet = Internet(calculator)
    private val fileManager = FileManager(context)
    private val internetRepository = InternetRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InternetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return InternetViewModel(internet, internetRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}