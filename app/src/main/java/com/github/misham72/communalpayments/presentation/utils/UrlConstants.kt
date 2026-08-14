package com.github.misham72.communalpayments.presentation.utils

@Suppress("HardcodedStringLiteral")
object UrlConstants {
    const val HTTP_PROTOCOL = "http://"
    const val HTTPS_PROTOCOL = "https://"
    const val DEFAULT_PROTOCOL = "https://"
}

fun String.normalizeUrl(): String {
    return if (startsWith(UrlConstants.HTTP_PROTOCOL) || startsWith(UrlConstants.HTTPS_PROTOCOL)) {
        this
    } else {
        // "${UrlConstants.DEFAULT_PROTOCOL}$this"
        UrlConstants.DEFAULT_PROTOCOL + this
    }
}
