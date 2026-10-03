package com.github.misham72.communalpayments.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.github.misham72.communalpayments.data.worker.NotificationScheduler
import com.github.misham72.communalpayments.di.AppContainer

class CommunalPaymentsApp : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)// — создаётся склад

        // Инициализация WorkManager с нашей фабрикой
        val config: Configuration = Configuration.Builder()
            .setWorkerFactory(appContainer.workerFactory)
            .build()
        WorkManager.initialize(this, config)//— настройка WorkManager

        // Периодическая проверка уведомлений (раз в час)
        NotificationScheduler.schedulePeriodic(this)// — постановка задачи
    }
}
