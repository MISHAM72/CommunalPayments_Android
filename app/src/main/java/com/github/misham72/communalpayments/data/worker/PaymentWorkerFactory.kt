package com.github.misham72.communalpayments.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.github.misham72.communalpayments.data.local.preferences.AccountPreferences
import com.github.misham72.communalpayments.domain.repository.SelectedServicesRepository

class PaymentWorkerFactory(
    private val accountPrefs: AccountPreferences,
    private val selectedServicesRepository: SelectedServicesRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        PaymentNotificationWorker::class.java.name ->
            PaymentNotificationWorker(appContext, workerParameters, accountPrefs, selectedServicesRepository)

        else -> null
    }
}
