package com.github.misham72.communalpayments.data.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

@Suppress("HardcodedStringLiteral")
object NotificationScheduler {   // object NotificationScheduler — это объявление синглтона (одного экземпляра на всё приложение).
    private const val WORK_NAME = "payment_notification_work"   // Все строковые литералы, которые повторяются или имеют специальное значение, должны быть вынесены в именованные константы.

    fun schedulePeriodic(context: Context) {  // Что делает: Запускает периодическую задачу (Worker), которая будет выполняться каждые 60 минут
        Log.d("NotificationScheduler", "schedulePeriodic вызван")
        val constraints = Constraints.Builder()   // Constraints.Builder() — создаёт строителя (builder) для набора правил.
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)    // setRequiredNetworkType(NetworkType.NOT_REQUIRED) — говорит, что задаче не нужен интернет.
            .build()   // .build() — завершает построение и возвращает объект Constraints.


        val workRequest = PeriodicWorkRequestBuilder<PaymentNotificationWorker>(   // — создаёт билдер для периодической задачи. Передаём число 60 и единицу времени TimeUnit.MINUTES → интервал 60 минут.
            60, TimeUnit.MINUTES
        ).setConstraints(constraints)   // .setConstraints(constraints) — применяет ограничения (из пункта выше).
            .build()   // .build() — создаёт объект workRequest.

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(   // WorkManager.getInstance(context) — получает экземпляр WorkManager(системный сервис для фоновых работ). Ему нужен context, enqueueUniquePeriodicWork(...) — ставит задачу в
            // очередь с уникальным именем.
            WORK_NAME,   // WORK_NAME — строка "payment_notification_work" (уникальный идентификатор).
            ExistingPeriodicWorkPolicy.KEEP,   // ExistingPeriodicWorkPolicy.KEEP — если уже есть задача с таким именем, не заменять её, а оставить старую.
            workRequest   // workRequest — сама задача, которую создали выше.
        )
        Log.d("NotificationScheduler", "WorkRequest создан и поставлен в очередь")
    }
}
