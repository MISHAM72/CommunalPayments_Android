package com.github.misham72.communalpayments.presentation.screen.screens.osago

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.OsagoRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.Osago


class OsagoViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val osago = Osago()
    private val fileManager = FileManager(context)
    private val osagoRepository = OsagoRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OsagoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return OsagoViewModel(osago, osagoRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
