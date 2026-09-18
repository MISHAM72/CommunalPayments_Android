package com.github.misham72.communalpayments.domain.repository

import com.github.misham72.communalpayments.domain.model.SelectedServices

interface SelectedServicesRepository {
    /** Загрузить текущий набор выбранных услуг. */
    fun getSelected(): SelectedServices //Пункт договора: «Уметь сообщить, какие услуги выбраны».

    /** Сохранить набор целиком. */
    fun saveSelected(services: SelectedServices) //Пункт договора: «Уметь сохранить набор услуг».

    /** Переключить одну услугу. */
    fun toggle(key: String)  //Пункт договора: «Уметь переключить одну услугу».

    /**
     * Инициализация для существующих пользователей:
     * если у пользователя уже есть история по каким-то услугам,
     * включаем их автоматически.
     */
    fun initializeFromExistingServices(existingServiceKeys: Set<String>) //Пункт договора: «Уметь мигрировать старых пользователей».
}
