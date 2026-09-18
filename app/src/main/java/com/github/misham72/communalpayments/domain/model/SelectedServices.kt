package com.github.misham72.communalpayments.domain.model

/**
 * Набор выбранных пользователем услуг.
 * Хранит только ключи (например, "electricity", "gas").
 */
data class SelectedServices(
    val keys: Set<String> = emptySet()
) {
    fun contains(key: String): Boolean = key in keys

    fun toggle(key: String): SelectedServices {
        val newKeys = if (key in keys) keys - key else keys + key
        return copy(keys = newKeys)
    }

}
