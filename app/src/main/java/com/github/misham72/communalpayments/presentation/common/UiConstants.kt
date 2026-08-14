package com.github.misham72.communalpayments.presentation.common

import java.util.Locale

@Suppress("HardcodedStringLiteral")
object UiConstants {
    const val DATE_OUTPUT_PATTERN = "d MMMM yyyy"
    val DEFAULT_LOCALE: Locale = Locale.forLanguageTag("ru")
    const val DATE_TIME_REGEX_PATTERN = """(\d{4}-\d{2}-\d{2}) \d{2}:\d{2}:\d{2}"""
    // другие UI-константы, например:
    // const val DATE_OUTPUT_PATTERN = "d MMMM yyyy"
}
