package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.InternetData


interface InternetRepository {
    suspend fun saveInternetPayment(data: InternetData)
}
