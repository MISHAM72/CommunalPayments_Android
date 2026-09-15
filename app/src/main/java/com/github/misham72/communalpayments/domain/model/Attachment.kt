package com.github.misham72.communalpayments.domain.model

/**
 * Одно вложение к записи истории.
 *
 * @param path     абсолютный путь к файлу
 * @param name     оригинальное имя файла
 * @param mimeType MIME-тип (image/jpeg, application/pdf, ...)
 */
data class Attachment(
    val path: String,
    val name: String,
    val mimeType: String
)
