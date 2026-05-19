package com.github.misham72.communalpayments.presentation.screen.screens.electricity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.ElectricityRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.Electricity

class ElectricityViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val electricity = Electricity()
    private val fileManager = FileManager(context)
    private val electricityRepository = ElectricityRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectricityViewModel::class.java)) {
            @Suppress("HardcodedStringLiteral")
            return ElectricityViewModel(electricity, electricityRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
