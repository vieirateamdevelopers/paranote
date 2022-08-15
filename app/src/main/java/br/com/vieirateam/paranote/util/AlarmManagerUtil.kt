package br.com.vieirateam.paranote.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import br.com.vieirateam.paranote.entity.Backup
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.service.BackupService
import br.com.vieirateam.paranote.service.NotificationService

object AlarmManagerUtil {

    fun create(context: Context, bundle: Bundle, note: Note) {
        val intent = getNotificationService(context)
        bundle.putSerializable(ConstantsUtil.ITEM, note)
        intent.putExtra(ConstantsUtil.BUNDLE, bundle)
        val requestCode = note.id.toInt()
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val pendingIntentTemp = PendingIntent.getBroadcast(context, (ConstantsUtil.REQUEST_CODE_PENDING_TEMP + requestCode), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = note.reminder.dateTime

        if (note.reminder.repeat > 0) {
            val intervalMillis = ConstantsUtil.MILLISECONDS * note.reminder.repeat
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, intervalMillis, pendingIntent)
            if (note.reminder.advance > 0) {
                val advanceTimeInMillis = calendar.timeInMillis - note.reminder.advance
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, advanceTimeInMillis, intervalMillis, pendingIntentTemp)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                if (note.reminder.advance > 0) {
                    val advanceTimeInMillis = calendar.timeInMillis - note.reminder.advance
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, advanceTimeInMillis, pendingIntentTemp)
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                if (note.reminder.advance > 0) {
                    val advanceTimeInMillis = calendar.timeInMillis - note.reminder.advance
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, advanceTimeInMillis, pendingIntentTemp)
                }
            }
        }
    }

    fun create(context: Context, bundle: Bundle, backup: Backup) {
        val intent = getBackupService(context)
        bundle.putSerializable(ConstantsUtil.BACKUP, backup)
        intent.putExtra(ConstantsUtil.BUNDLE, bundle)
        val pendingIntent = PendingIntent.getBroadcast(context, backup.requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (backup.repeat) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, backup.calendar.timeInMillis, backup.intervalMillis, pendingIntent)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, backup.calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, backup.calendar.timeInMillis, pendingIntent)
            }
        }
    }

    fun cancel(context: Context, intent: Intent, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
    }

    fun getBackupService(context: Context) = Intent(context, BackupService::class.java)

    fun getNotificationService(context: Context) = Intent(context, NotificationService::class.java)
}
