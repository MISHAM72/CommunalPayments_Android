package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.incomes.SelectedIncomeCategories

interface SelectedIncomeCategoriesRepository {
    /** Загрузить текущий набор выбранных категорий. */
    fun getSelected(): SelectedIncomeCategories

    /** Сохранить набор целиком. */
    fun saveSelected(categories: SelectedIncomeCategories)

    /** Переключить одну категорию. */
    fun toggle(key: String)

    /**
     * Инициализация для существующих пользователей:
     * если у пользователя уже есть записи доходов по каким-то категориям,
     * включаем их автоматически.
     */
    fun initializeFromExisting(existingKeys: Set<String>)
}
