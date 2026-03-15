package com.github.misham72.communalpayments.presentation.screen.screens.troyka

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.misham72.communalpayments.data.local.AccountPreferences
import com.github.misham72.communalpayments.data.local.FileManager
import com.github.misham72.communalpayments.data.repository.TroykaRepositoryImpl
import com.github.misham72.communalpayments.domain.userclasses.Troyka

class TroykaViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {
    private val troyka = Troyka()
    private val fileManager = FileManager(context)
    private val troykaRepository = TroykaRepositoryImpl(context, fileManager)
    private val accountPrefs = AccountPreferences(context.applicationContext)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TroykaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return TroykaViewModel(troyka, troykaRepository, accountPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
