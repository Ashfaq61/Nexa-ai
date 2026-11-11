package com.example.services

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

data class CapturedNotification(
    val id: String,
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long
)

class NexusNotificationListener : NotificationListenerService() {

    companion object {
        var instance: NexusNotificationListener? = null
        val recentNotifications = mutableListOf<CapturedNotification>()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        val extras = sbn.notification.extras ?: return
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        if (title.isNotBlank() || text.isNotBlank()) {
            val record = CapturedNotification(
                id = "${sbn.packageName}_${sbn.id}_${sbn.postTime}",
                packageName = sbn.packageName,
                title = title,
                text = text,
                timestamp = sbn.postTime
            )
            synchronized(recentNotifications) {
                if (recentNotifications.size > 50) {
                    recentNotifications.removeAt(0)
                }
                recentNotifications.add(record)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
