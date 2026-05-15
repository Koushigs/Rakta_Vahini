package com.raktavahini.app.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object DonationReminderScheduler {
    private const val UNIQUE_WORK_NAME = "thank_you_notification"

    fun schedule(context: Context) {
        val request = OneTimeWorkRequestBuilder<ThankYouNotificationWorker>()
            .setInitialDelay(1, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }
}
