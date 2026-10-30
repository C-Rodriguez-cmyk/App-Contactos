package com.my.first.app_contactos.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.my.first.app_contactos.MainActivity
import com.my.first.app_contactos.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getStringExtra("EXTRA_NOTE_ID") ?: return
        val title = intent.getStringExtra("EXTRA_NOTE_TITLE") ?: "Recordatorio de Notely"
        val content = intent.getStringExtra("EXTRA_NOTE_CONTENT") ?: ""
        val contactName = intent.getStringExtra("EXTRA_CONTACT_NAME")

        val channelId = "notely_reminders_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de Notely",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de notas y tareas pendientes con contactos"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            noteId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val bodyText = if (!contactName.isNullOrBlank()) {
            if (content.isNotBlank()) "Pendiente con $contactName: $content" else "Pendiente con $contactName"
        } else {
            content.ifBlank { "Tienes un recordatorio pendiente en Notely" }
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_logo_notely)
            .setContentTitle("⏰ $title")
            .setContentText(bodyText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(noteId.hashCode(), notification)
    }
}
