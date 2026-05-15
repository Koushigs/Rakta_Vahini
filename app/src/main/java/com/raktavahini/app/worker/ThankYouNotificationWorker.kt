package com.raktavahini.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.raktavahini.app.R

class ThankYouNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val channelId = ensureChannel(applicationContext)
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Thank you for donating")
            .setContentText("Your last donation was saved. You helped keep the directory ready for the next emergency.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
        return Result.success()
    }

    private fun ensureChannel(context: Context): String {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Donation reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        return CHANNEL_ID
    }

    companion object {
        const val CHANNEL_ID = "thank_you_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
