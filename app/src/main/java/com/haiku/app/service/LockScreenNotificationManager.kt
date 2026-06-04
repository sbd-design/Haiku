package com.haiku.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.haiku.app.R
import com.haiku.app.data.db.HaikuEntity
import com.haiku.app.ui.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockScreenNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val HAIKU_CHANNEL_ID = "haiku_lockscreen"
        const val SERVICE_CHANNEL_ID = "haiku_service"
        const val HAIKU_NOTIFICATION_ID = 1001
        const val SERVICE_NOTIFICATION_ID = 1000
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        // Silent channel for the haiku on the lock screen
        NotificationChannel(
            HAIKU_CHANNEL_ID,
            context.getString(R.string.channel_haiku_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.channel_haiku_description)
            setShowBadge(false)
            enableVibration(false)
            enableLights(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(this)
        }

        // Minimal channel to keep the foreground service alive
        NotificationChannel(
            SERVICE_CHANNEL_ID,
            context.getString(R.string.channel_service_name),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            setShowBadge(false)
            enableVibration(false)
            notificationManager.createNotificationChannel(this)
        }
    }

    fun buildServiceNotification(): Notification {
        val openIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, SERVICE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_haiku)
            .setContentTitle(context.getString(R.string.service_running))
            .setContentIntent(openIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    fun updateLockScreenHaiku(haiku: HaikuEntity) {
        val openIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val haikuText = "${haiku.line1}\n${haiku.line2}\n${haiku.line3}"

        val notification = NotificationCompat.Builder(context, HAIKU_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_haiku)
            .setContentTitle(haiku.appName)
            .setContentText(haiku.line1)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(haikuText)
                    .setSummaryText(haiku.appName)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setContentIntent(openIntent)
            .build()

        notificationManager.notify(HAIKU_NOTIFICATION_ID, notification)
    }

    fun cancelHaiku() {
        notificationManager.cancel(HAIKU_NOTIFICATION_ID)
    }
}
