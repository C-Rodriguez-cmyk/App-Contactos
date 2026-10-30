package com.my.first.app_contactos.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.my.first.app_contactos.model.Note
import java.text.SimpleDateFormat
import java.util.Locale

object NotificationScheduler {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(context: Context, note: Note) {
        if (!note.isTask || note.isCompleted) {
            cancelNotification(context, note.id)
            return
        }

        val dateTimeStr = when {
            !note.reminderDate.isNullOrBlank() && !note.reminderTime.isNullOrBlank() ->
                "${note.reminderDate}, ${note.reminderTime}"
            !note.reminderDate.isNullOrBlank() ->
                note.reminderDate
            else -> null
        }

        val triggerTimeMillis = parseReminderTimeToMillis(dateTimeStr) ?: return
        if (triggerTimeMillis <= System.currentTimeMillis()) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("EXTRA_NOTE_ID", note.id)
            putExtra("EXTRA_NOTE_TITLE", note.title)
            putExtra("EXTRA_NOTE_CONTENT", note.content)
            putExtra("EXTRA_CONTACT_NAME", note.contactName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
        } catch (_: Exception) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        }
    }

    fun cancelNotification(context: Context, noteId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun parseReminderTimeToMillis(dateTimeStr: String?): Long? {
        if (dateTimeStr.isNullOrBlank()) return null
        val formats = arrayOf(
            "dd/MM/yyyy, HH:mm",
            "dd/MM/yyyy, hh:mm a",
            "dd/MM/yyyy HH:mm",
            "dd/MM/yyyy"
        )
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                val date = sdf.parse(dateTimeStr.trim())
                if (date != null) {
                    return date.time
                }
            } catch (_: Exception) { }
        }
        return null
    }
}
