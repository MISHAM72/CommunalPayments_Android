package com.github.misham72.communalpayments.domain.model.incomes

/**
 * Набор выбранных пользователем категорий доходов.
 * Хранит только ключи (например, "SALARY", "SIDE_JOB").
 */
data class SelectedIncomeCategories(
    val keys: Set<String> = emptySet()
) {
    fun contains(key: String): Boolean = key in keys

    fun toggle(key: String): SelectedIncomeCategories {
        val newKeys = if (key in keys) keys - key else keys + key
        return copy(keys = newKeys)
    }
}
