package com.github.misham72.communalpayments.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.misham72.communalpayments.R
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.constants.ServiceKeys
import com.github.misham72.communalpayments.domain.repository.SelectedServicesRepository
import com.github.misham72.communalpayments.domain.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentNotificationWorker(
    context: Context,
    params: WorkerParameters,
    private val accountPrefs: AccountPreferences,
    private val selectedServicesRepository: SelectedServicesRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val serviceNames = mapOf(//Это Map — словарь из пар «ключ → значение».
            ServiceKeys.ELECTRICITY to applicationContext.getString(R.string.service_display_name_electricity),
            ServiceKeys.GAS to applicationContext.getString(R.string.service_display_name_gas),
            ServiceKeys.COLDWATER to applicationContext.getString(R.string.service_display_name_coldwater),
            ServiceKeys.HOTWATER to applicationContext.getString(R.string.service_display_name_hotwater),
            ServiceKeys.DRAINAGE to applicationContext.getString(R.string.service_display_name_drainage),
            ServiceKeys.GARBAGE to applicationContext.getString(R.string.service_display_name_garbage),
            ServiceKeys.ZONT to applicationContext.getString(R.string.service_display_name_zont),
            ServiceKeys.INTERNET to applicationContext.getString(R.string.service_display_name_internet),
            ServiceKeys.MTS to applicationContext.getString(R.string.service_display_name_mts),
            ServiceKeys.TINKOFF to applicationContext.getString(R.string.service_display_name_tinkoff),
            ServiceKeys.TAXES to applicationContext.getString(R.string.service_display_name_taxes),
            ServiceKeys.TROYKA to applicationContext.getString(R.string.service_display_name_troyka),
            ServiceKeys.OSAGO to applicationContext.getString(R.string.service_display_name_osago),
            ServiceKeys.HOSTEL to applicationContext.getString(R.string.service_display_name_hostel),
            ServiceKeys.CAPITAL_REPAIR to applicationContext.getString(R.string.service_display_name_capital_repair)
        )

        val selectedKeys = selectedServicesRepository.getSelected().keys
        for ((key, name) in serviceNames) {
            if (key !in selectedKeys) continue   // ← пропустить не выбранные

            val dateStr = accountPrefs.getCustomDate(key)
            if (dateStr.isNotBlank()) {
                val daysLeft = DateUtils.daysUntil(dateStr)
                if (daysLeft <= 3) {
                    showNotification(applicationContext, key, name, daysLeft)
                }
            }
        }
        Result.success()
    }

    private fun showNotification(context: Context, key: String, serviceName: String, daysLeft: Int) {
        // Проверка разрешения POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }
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


        val message = when {
            daysLeft == 3 -> context.getString(R.string.payment_in_3_days, serviceName)
            daysLeft == 2 -> context.getString(R.string.payment_in_2_days, serviceName)
            daysLeft == 1 -> context.getString(R.string.payment_tomorrow, serviceName)
            daysLeft == 0 -> context.getString(R.string.payment_today, serviceName)
            daysLeft == -1 -> context.getString(R.string.payment_overdue_1, serviceName)
            daysLeft == -2 -> context.getString(R.string.payment_overdue_2, serviceName)
            daysLeft == -3 -> context.getString(R.string.payment_overdue_3, serviceName)
            daysLeft <= -4 -> context.getString(R.string.payment_overdue_many, -daysLeft, serviceName)
            else -> return
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationId = key.hashCode()
        notificationManager.notify(notificationId, notification)

    }
}
