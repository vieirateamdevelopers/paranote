package br.com.vieirateam.paranote.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.activity.MainActivity
import br.com.vieirateam.paranote.entity.Note
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationUtil {

    fun create(note: Note) {
        if (checkReminder(note.reminder.dateTime)) {
            val milliSeconds = note.reminder.dateTime.timeInMillis
            val seconds = fromMilliSecondsToSeconds(milliSeconds)
            if (note.reminder.advance == 0.toLong()){
                create(note, seconds, false)
            } else {
                val advanceSeconds = seconds - note.reminder.advance
                create(note, advanceSeconds, true)
                create(note, seconds, false)
            }
        }
    }

    fun cancel(note: Note) {
        val context: Context = NoteApplication.getInstance().applicationContext
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag("${note.id}")
        if (note.reminder.advance > 0) {
            workManager.cancelAllWorkByTag("advance_${note.id}")
        }
    }

    fun build(intent: Intent, note: Note) {
        val context: Context = NoteApplication.getInstance().applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                ConstantsUtil.CHANNEL_ID,
                context.getString(R.string.app_chanel),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableVibration(true)
                setShowBadge(true)
                enableLights(true)
                description = context.getString(R.string.app_name)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val stackBuilder = TaskStackBuilder.create(context).apply {
            addParentStack(MainActivity::class.java)
            addNextIntent(intent)
        }

        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, ConstantsUtil.CHANNEL_ID).apply {
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            setSmallIcon(R.drawable.ic_drawable_notification_icon)
            setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(pendingIntent)
            setGroup(ConstantsUtil.KEY_GROUP)
            if (note.title.isNotEmpty()) {
                setContentTitle(note.title)
            }
            setStyle(NotificationCompat.BigTextStyle().bigText(note.body))
            setAutoCancel(true)
        }
        notificationManager.notify(notificationID, notification.build())
    }

    fun build(doBackup: Boolean) {
        val context: Context = NoteApplication.getInstance().applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                ConstantsUtil.CHANNEL_ID,
                context.getString(R.string.app_chanel),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.app_name)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(context, ConstantsUtil.CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_drawable_notification_icon)
            priority = NotificationCompat.PRIORITY_DEFAULT
            val title = if (doBackup) {
                context.getString(R.string.text_view_backup_doing)
            } else {
                context.getString(R.string.text_view_restore_doing)
            }
            setContentTitle(title)
        }

        Thread {
            kotlin.run {
                var i = 0
                    while (i <= 100) {
                        notification.setContentText("${i}%")
                        notification.setProgress(100, i, false)
                        notificationManager.notify(notificationID, notification.build())
                        try {
                            Thread.sleep(100)
                        } catch (exception: InterruptedException) {
                            Log.d(ConstantsUtil.TAG, exception.toString())
                        }
                        i += 1
                    }
                notification.setContentTitle(context.getText(R.string.app_name))
                val body = if (doBackup) {
                    context.getString(R.string.text_view_backup_finished)
                } else {
                    context.getString(R.string.text_view_restore_finished)
                }
                notification.setContentText(body)
                notification.setProgress(0, 0, false)
                notificationManager.notify(notificationID, notification.build())
            }
        }.start()
    }

    private fun create(note: Note, milliSeconds: Long, advance: Boolean) {
        val context: Context = NoteApplication.getInstance().applicationContext
        val workManager = WorkManager.getInstance(context)
        val request = OneTimeWorkRequest.Builder(NotificationWorker::class.java).apply {
            setInitialDelay(milliSeconds, TimeUnit.SECONDS)
            setInputData(getInputDataNote(note))
            setConstraints(getConstraints())
            if (advance) {
                addTag("advance_${note.id}")
            } else {
                addTag("${note.id}")
            }
        }
        workManager.enqueue(request.build())
    }

    private fun checkReminder(reminder: Calendar): Boolean {
        val calendar = Calendar.getInstance()
        val currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val reminderDayOfYear = reminder.get(Calendar.DAY_OF_YEAR)
        when {
            reminderDayOfYear > currentDayOfYear -> {
                return true
            }
            reminderDayOfYear == currentDayOfYear -> {
                val currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                val reminderHourOfDay = reminder.get(Calendar.HOUR_OF_DAY)
                return when {
                    reminderHourOfDay > currentHourOfDay -> {
                        true
                    }
                    reminderHourOfDay == currentHourOfDay -> {
                        val currentMinutes = calendar.get(Calendar.MINUTE)
                        val reminderMinutes = reminder.get(Calendar.MINUTE)
                        reminderMinutes > currentMinutes
                    } else -> {
                        false
                    }
                }
            } else -> {
                return false
            }
        }
    }

    private fun fromMilliSecondsToSeconds(milliSeconds: Long): Long {
        var currentSecond: Int
        val calendar = Calendar.getInstance().apply {
            currentSecond = get(Calendar.SECOND)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val resultSecond = (calendar.timeInMillis) / 1000
        return (((milliSeconds / 1000) - resultSecond) - currentSecond) - 5
    }

    private fun getConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .build()
    }

    private fun getInputDataNote(note: Note): Data {
        val json = SerializableUtil.serializable(note)
        return Data.Builder()
            .putString(ConstantsUtil.ITEM, json)
            .build()
    }
}