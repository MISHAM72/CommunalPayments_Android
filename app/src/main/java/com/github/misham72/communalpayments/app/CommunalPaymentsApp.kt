package com.github.misham72.communalpayments.app

import android.app.Application
import com.github.misham72.communalpayments.data.worker.NotificationScheduler
import com.github.misham72.communalpayments.di.AppContainer

//это стартовая площадка приложения, которая при запуске включает фоновый планировщик уведомлений.
class CommunalPaymentsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
        // Запускаем периодическую проверку уведомлений (раз в день)
        NotificationScheduler.schedulePeriodic(this)
    }
}
