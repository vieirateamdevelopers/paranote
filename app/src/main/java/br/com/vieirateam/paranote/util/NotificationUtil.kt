package br.com.vieirateam.paranote.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.activity.MainActivity
import br.com.vieirateam.paranote.entity.Note

object NotificationUtil {

    fun create(bundle: Bundle, note: Note) {
        val context: Context = NoteApplication.getInstance().applicationContext
        AlarmManagerUtil.create(context = context, bundle = bundle, note = note)
    }

    fun cancel(note: Note) {
        val context: Context = NoteApplication.getInstance().applicationContext
        val intent = AlarmManagerUtil.getNotificationService(context)
        AlarmManagerUtil.cancel(context, intent, note.id.toInt())
        if (note.reminder.advance > 0) {
            val requestCodeAdvance = ConstantsUtil.REQUEST_CODE_PENDING_TEMP + note.id.toInt()
            AlarmManagerUtil.cancel(context, intent, requestCodeAdvance)
        }
    }

    fun build(intent: Intent, note: Note) {
        val context: Context = NoteApplication.getInstance().applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                ConstantsUtil.CHANNEL_ID,
                context.getString(R.string.app_chanel_1),
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
            setSmallIcon(R.drawable.ic_drawable_notification)
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
                context.getString(R.string.app_chanel_2),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.app_name)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(context, ConstantsUtil.CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_drawable_notification)
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
}