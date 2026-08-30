package com.github.misham72.communalpayments.data.repository

import com.github.misham72.communalpayments.domain.common.BankLogoKeys
import com.github.misham72.communalpayments.domain.common.BankNames
import com.github.misham72.communalpayments.domain.model.Bank
import com.github.misham72.communalpayments.domain.repository.BankRepository

class BankRepositoryImpl : BankRepository {

    override fun getSupportedBanks(): List<Bank> {
        return listOf(
            Bank(BankNames.SBER, "ru.sberbankmobile", BankLogoKeys.SBER),
            Bank(BankNames.TBANK, "com.idamob.tinkoff.android", BankLogoKeys.TBANK),
            Bank(BankNames.VTB, "ru.vtb24.mobilebanking.android", BankLogoKeys.VTB),
            Bank(BankNames.ALFA, "ru.alfabank.mobile.android", BankLogoKeys.ALFA),
            Bank(BankNames.TOCHKA, "ru.zhuck.webapp", BankLogoKeys.TOCHKA),
            Bank(BankNames.YOOMONEY, "ru.yoo.money", BankLogoKeys.YOOMONEY),
            Bank(BankNames.BKS, "ru.bcs.bcsbank", BankLogoKeys.BKS),
            Bank(BankNames.OZON, "ru.ozon.fintech.finance", BankLogoKeys.OZON)
        )
    }
}
