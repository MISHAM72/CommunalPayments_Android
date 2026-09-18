package com.github.misham72.communalpayments.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.github.misham72.communalpayments.domain.model.SelectedServices
import com.github.misham72.communalpayments.domain.repository.SelectedServicesRepository

class SelectedServicesRepositoryImpl(
    private val prefs: SharedPreferences
) : SelectedServicesRepository {

    private companion object {
        private const val KEY_SELECTED_SERVICES = "selected_services"
    }

    override fun getSelected(): SelectedServices {
        val keys = prefs.getStringSet(KEY_SELECTED_SERVICES, null)
            ?: emptySet()
        return SelectedServices(keys)
    }

    override fun saveSelected(services: SelectedServices) {
        prefs.edit {
            putStringSet(KEY_SELECTED_SERVICES, services.keys)
        }
    }

    override fun toggle(key: String) {
        val current = getSelected()
        saveSelected(current.toggle(key))
    }

    override fun initializeFromExistingServices(existingServiceKeys: Set<String>) {
        // Если ключ ещё не создан — значит это первый запуск новой версии.
        // Тогда сохраняем услуги, по которым у пользователя уже есть история.
        val alreadyInitialized = prefs.contains(KEY_SELECTED_SERVICES)
        if (!alreadyInitialized) {
            saveSelected(SelectedServices(existingServiceKeys))
        }
    }
}
