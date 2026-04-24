package com.github.misham72.communalpayments.presentation.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.github.misham72.communalpayments.R

object BankPaymentHelper {

    // Класс для хранения информации о банке
    data class BankInfo(
        val name: String,        // Название банка для пользователя
        val packageName: String, // Уникальный идентификатор приложения
        val deepLink: String? = null // Ссылка для открытия конкретного раздела (пока не используем)
    )

    // Список поддерживаемых банков (пока один)
    @Suppress("HardcodedStringLiteral")
    val supportedBanks: List<BankInfo> = listOf(
        BankInfo("СберБанк", "ru.sberbankmobile"),
        BankInfo("Т-Банк", "com.idamob.tinkoff.android"),
        BankInfo("ВТБ", "ru.vtb24.mobilebanking.android"),
        BankInfo("Альфа-Банк", "ru.alfabank.mobile.android"),
        BankInfo("Точка Банк", "ru.zhuck.webapp"),
        BankInfo("ЮMoney", "ru.yoo.money"),
        BankInfo("БКС банк", "ru.bcs.bcsbank")
        // Здесь в будущем будут добавляться другие банки
    )

    // Функция для открытия приложения банка
    fun openBankApp(context: Context, bank: BankInfo) {
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
    private fun openInStore(context: Context, bank: BankInfo) {
        try {
            val storeIntent = Intent(Intent.ACTION_VIEW, "market://details?id=${bank.packageName}".toUri())
            context.startActivity(storeIntent)
        } catch (_: Exception) {
            Toast.makeText(
                context, context.getString(R.string.could_not_open_the_app_store), Toast.LENGTH_SHORT
            ).show()
        }
    }
}