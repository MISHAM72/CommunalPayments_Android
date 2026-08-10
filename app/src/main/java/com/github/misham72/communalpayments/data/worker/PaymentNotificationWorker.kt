package com.github.misham72.communalpayments.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.utils.DateUtils
import com.github.misham72.communalpayments.domain.utils.ServiceKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PaymentNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val appContext = applicationContext
        val accountPrefs = AccountPreferences(appContext)

        val serviceNames = mapOf(
            ServiceKeys.ELECTRICITY to appContext.getString(R.string.service_display_name_electricity),
            ServiceKeys.GAS to appContext.getString(R.string.service_display_name_gas),
            ServiceKeys.WATER to appContext.getString(R.string.service_display_name_water),
            ServiceKeys.GARBAGE to appContext.getString(R.string.service_display_name_garbage),
            ServiceKeys.ZONT to appContext.getString(R.string.service_display_name_zont),
            ServiceKeys.INTERNET to appContext.getString(R.string.service_display_name_internet),
            ServiceKeys.MTS to appContext.getString(R.string.service_display_name_mts),
            ServiceKeys.TINKOFF to appContext.getString(R.string.service_display_name_tinkoff),
            ServiceKeys.TAXES to appContext.getString(R.string.service_display_name_taxes),
            ServiceKeys.TROYKA to appContext.getString(R.string.service_display_name_troyka),
            ServiceKeys.OSAGO to appContext.getString(R.string.service_display_name_osago),
            ServiceKeys.HOSTEL to appContext.getString(R.string.service_display_name_hostel)
        )

        for ((key, name) in serviceNames) {
            val dateStr = accountPrefs.getCustomDate(key)
            if (dateStr.isNotBlank()) {
                val daysLeft = DateUtils.daysUntil(dateStr)
                if (daysLeft in 1..3) {
                    showNotification(appContext, key, name, daysLeft)
                    delay(300) // задержка 300 миллисекунд
                }
            }
        }
        Result.success()
    }

    private fun showNotification(context: Context, key: String, serviceName: String, daysLeft: Int) {
        // Уникальный ID канала для каждой услуги
        val channelId = context.getString(R.string.payment_reminder_channel) + "_" + key
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаём канал с уникальным именем
        val channel = NotificationChannel(
            channelId,
            context.getString(R.string.notifications, serviceName), // человекочитаемое имя
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.notifications_about_upcoming_service_payments)
        }
        notificationManager.createNotificationChannel(channel)


        val message = when (daysLeft) {
            1 -> context.getString(R.string.payment_tomorrow, serviceName)
            else -> context.getString(R.string.days_payment_for, daysLeft, serviceName)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.winter_house)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationId = key.hashCode()
        notificationManager.notify(notificationId, notification)

    }
}
