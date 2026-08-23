package com.github.misham72.communalpayments.domain.model

data class Receipt(
    val id: String,
    val serviceKey: String,
    val fileName: String,
    val savedDate: Long,
    val filePath: String
)
