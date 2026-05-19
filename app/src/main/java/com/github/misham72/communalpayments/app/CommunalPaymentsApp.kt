package com.github.misham72.communalpayments.app

import android.app.Application
import com.github.misham72.communalpayments.data.worker.NotificationScheduler

class CommunalPaymentsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Запускаем периодическую проверку уведомлений (раз в день)
        NotificationScheduler.schedulePeriodic(this)
    }
}
