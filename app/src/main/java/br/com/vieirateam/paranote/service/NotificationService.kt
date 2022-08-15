package br.com.vieirateam.paranote.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import br.com.vieirateam.paranote.activity.MainActivity
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.util.CalendarUtil
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.util.NotificationUtil

class NotificationService: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.getBundleExtra(ConstantsUtil.BUNDLE) as Bundle
        val note = bundle.getSerializable(ConstantsUtil.ITEM) as Note
        if (CalendarUtil.canNotify(note.reminder.dateTime)) {
            if (note.reminder.advance > 0) {
                val advanceTimeInMillis = note.reminder.dateTime.timeInMillis - note.reminder.advance
                val advanceCalendar = CalendarUtil.toTimestampFromCalendar(advanceTimeInMillis)
                if (CalendarUtil.canNotify(advanceCalendar)) {
                    createNotification(context, note)
                }
            }
            createNotification(context, note)
        }
    }

    private fun createNotification(context: Context, note: Note) {
        val intent = Intent(context, MainActivity::class.java)
        NotificationUtil.build(intent, note)
    }
}