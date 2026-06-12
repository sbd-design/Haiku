package com.haiku.app.service

import android.app.Notification
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.haiku.app.api.HaikuGenerator
import com.haiku.app.api.NotificationInput
import com.haiku.app.data.HaikuRepository
import com.haiku.app.data.datastore.SettingsDataStore
import com.haiku.app.data.db.HaikuEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HaikuNotificationListenerService : NotificationListenerService() {

    @Inject lateinit var haikuGenerator: HaikuGenerator
    @Inject lateinit var repository: HaikuRepository
    @Inject lateinit var lockScreenManager: LockScreenNotificationManager
    @Inject lateinit var settingsDataStore: SettingsDataStore

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationQueue = mutableListOf<StatusBarNotification>()
    private var debounceJob: Job? = null

    // System packages whose notifications should never become haikus
    private val systemPackagesPrefixes = listOf(
        "com.android.",
        "android",
        "com.google.android.gms",
        "com.google.android.gsf",
        packageName
    )

    override fun onListenerConnected() {
        super.onListenerConnected()
        startForeground(
            LockScreenNotificationManager.SERVICE_NOTIFICATION_ID,
            lockScreenManager.buildServiceNotification()
        )
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        requestRebind(componentName)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (isNoise(sbn)) return

        serviceScope.launch {
            val blocked = settingsDataStore.blockedPackages.first()
            if (sbn.packageName in blocked) return@launch

            notificationQueue.add(sbn)
            debounceJob?.cancel()
            debounceJob = serviceScope.launch {
                delay(2_000)
                processLatest()
            }
        }
    }

    private suspend fun processLatest() {
        val sbn = notificationQueue.lastOrNull() ?: return
        notificationQueue.clear()

        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: return

        val appName = getAppName(sbn.packageName)

        val haiku = haikuGenerator.generate(
            NotificationInput(
                appName = appName,
                sender = title,
                text = text
            )
        ) ?: return

        val entity = HaikuEntity(
            line1 = haiku.line1,
            line2 = haiku.line2,
            line3 = haiku.line3,
            appName = appName,
            packageName = sbn.packageName,
            originalText = if (title != null) "$title: $text" else text
        )

        repository.saveHaiku(entity)
        lockScreenManager.updateLockScreenHaiku(entity)
    }

    private fun isNoise(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification
        val extras = notification.extras

        if (systemPackagesPrefixes.any { sbn.packageName.startsWith(it) }) return true
        if (notification.flags and Notification.FLAG_FOREGROUND_SERVICE != 0) return true
        if (notification.flags and Notification.FLAG_ONGOING_EVENT != 0) return true

        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        if (text.isNullOrBlank()) return true

        return false
    }

    private fun getAppName(packageName: String): String {
        return try {
            packageManager
                .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                .let { packageManager.getApplicationLabel(it).toString() }
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        debounceJob?.cancel()
    }
}
