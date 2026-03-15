package com.github.misham72.communalpayments.presentation.screen.screens.water

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.WaterRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.Water

class WaterViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val water = Water()
    private val fileManager = FileManager(context)
    private val waterRepository = WaterRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return WaterViewModel(water, waterRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}