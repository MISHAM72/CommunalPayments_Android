package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.ZONT

interface ZONTRepository {
    fun saveZONTPayment(data: ZONT.ZONTData)
}