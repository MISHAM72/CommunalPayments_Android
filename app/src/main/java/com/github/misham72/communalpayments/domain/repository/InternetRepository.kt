package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.userclasses.Internet

interface InternetRepository {
    fun saveInternetPayment(data: Internet.InternetData)
}