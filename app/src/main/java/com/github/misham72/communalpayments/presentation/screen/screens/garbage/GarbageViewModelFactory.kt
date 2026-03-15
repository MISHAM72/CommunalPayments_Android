package com.github.misham72.communalpayments.presentation.screen.screens.garbage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.GarbageRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.Garbage

class GarbageViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val garbage = Garbage()
    private val fileManager = FileManager(context)
    private val garbageRepository = GarbageRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GarbageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GarbageViewModel(garbage, garbageRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}