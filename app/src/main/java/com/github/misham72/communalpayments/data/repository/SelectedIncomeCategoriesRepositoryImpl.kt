package com.github.misham72.communalpayments.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.misham72.communalpayments.data.common.DataConstants
import com.github.misham72.communalpayments.domain.model.incomes.SelectedIncomeCategories
import com.github.misham72.communalpayments.domain.repository.SelectedIncomeCategoriesRepository

class SelectedIncomeCategoriesRepositoryImpl(
    private val prefs: SharedPreferences
) : SelectedIncomeCategoriesRepository {

    override fun getSelected(): SelectedIncomeCategories {
        val keys = prefs.getStringSet(DataConstants.KEY_SELECTED_INCOME_CATEGORIES, null)
            ?: emptySet()
        return SelectedIncomeCategories(keys)
    }

    override fun saveSelected(categories: SelectedIncomeCategories) {
        prefs.edit {
            putStringSet(DataConstants.KEY_SELECTED_INCOME_CATEGORIES, categories.keys)
        }
    }

    override fun toggle(key: String) {
        val current = getSelected()
        saveSelected(current.toggle(key))
    }

    override fun initializeFromExisting(existingKeys: Set<String>) {
        // Если ключ ещё не создан — значит это первый запуск новой версии.
        // Тогда сохраняем категории, по которым у пользователя уже есть записи.
        val alreadyInitialized = prefs.contains(DataConstants.KEY_SELECTED_INCOME_CATEGORIES)
        if (!alreadyInitialized) {
            saveSelected(SelectedIncomeCategories(existingKeys))
        }
    }
}
