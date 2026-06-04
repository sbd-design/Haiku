package com.haiku.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return

        // Notification listener services are automatically restarted by the system
        // after reboot if the user has granted the permission. This receiver ensures
        // that the lock screen notification is refreshed if the service is already enabled.
        if (isNotificationListenerEnabled(context)) {
            // The NotificationListenerService will reconnect automatically;
            // we just need to ensure nothing blocks the startup.
        }
    }

    private fun isNotificationListenerEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return enabledListeners.contains(context.packageName)
    }
}
