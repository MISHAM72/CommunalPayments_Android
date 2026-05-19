package com.github.misham72.communalpayments.presentation.screen.screens.gas

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.GasRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.Gas

class GasViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val gas = Gas()
    private val fileManager = FileManager(context)
    private val gasRepository = GasRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GasViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return GasViewModel(gas, gasRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
