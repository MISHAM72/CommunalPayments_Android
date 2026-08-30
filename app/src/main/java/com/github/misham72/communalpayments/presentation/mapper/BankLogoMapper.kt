package com.github.misham72.communalpayments.presentation.mapper

import androidx.annotation.DrawableRes
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.domain.common.BankLogoKeys

object BankLogoMapper {
    @DrawableRes
    fun getLogoResId(logoKey: String): Int {
        return when (logoKey) {
            BankLogoKeys.SBER -> R.drawable.ic_sberbank
            BankLogoKeys.TBANK -> R.drawable.ic_tbank
            BankLogoKeys.VTB -> R.drawable.ic_vtb
            BankLogoKeys.ALFA -> R.drawable.ic_alfa
            BankLogoKeys.TOCHKA -> R.drawable.ic_tochka
            BankLogoKeys.YOOMONEY -> R.drawable.ic_yoomoney
            BankLogoKeys.BKS -> R.drawable.ic_bks
            BankLogoKeys.OZON -> R.drawable.ic_ozon
            else -> R.drawable.ic_bank_default
        }
    }
}
