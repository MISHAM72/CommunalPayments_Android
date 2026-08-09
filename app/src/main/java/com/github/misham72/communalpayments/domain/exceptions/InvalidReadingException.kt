package com.github.misham72.communalpayments.domain.exceptions

class InvalidReadingException(message: String) : RuntimeException(message) {
    companion object {
        private const val serialVersionUID = 1L   // ← добавляем эту строчку
    }
}
