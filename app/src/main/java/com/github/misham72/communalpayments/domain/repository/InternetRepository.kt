package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.InternetData

//Назначение: сохранить информацию об оплате интернет-услуг.
interface InternetRepository {
    suspend fun saveInternetPayment(data: InternetData)
}
