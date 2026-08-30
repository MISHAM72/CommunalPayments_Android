package com.github.misham72.communalpayments.presentation.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.model.Bank

object BankPaymentHelper {


    // Функция для открытия приложения банка
    fun openBankApp(context: Context, bank: Bank) {
        val intent = context.packageManager.getLaunchIntentForPackage(bank.packageName)
        if (intent != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context, context.getString(R.string.app_is_not_installed, bank.name), Toast.LENGTH_SHORT
            ).show()
            openInStore(context, bank)
        }
    }

    // Функция для открытия страницы приложения в магазине
    private fun openInStore(context: Context, bank: Bank) {
        try {
            @Suppress("HardcodedStringLiteral")
            val storeIntent = Intent(Intent.ACTION_VIEW, "market://details?id=${bank.packageName}".toUri())
            context.startActivity(storeIntent)
        } catch (_: Exception) {
            Toast.makeText(
                context, context.getString(R.string.could_not_open_the_app_store), Toast.LENGTH_SHORT
            ).show()
        }
    }
}
