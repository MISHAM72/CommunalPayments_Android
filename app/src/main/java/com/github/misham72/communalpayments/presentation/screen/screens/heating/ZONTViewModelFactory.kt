package com.github.misham72.communalpayments.presentation.screen.screens.heating

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.ZONTRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.ZONT


class ZONTViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val zont = ZONT()
    private val fileManager = FileManager(context)
    private val zontRepository = ZONTRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ZONTViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return ZONTViewModel(zont, zontRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}