package br.com.vieirateam.paranote.util

import java.util.Calendar
import java.util.GregorianCalendar

object CalendarUtil {

    fun toTimestampFromCalendar(value: Long): Calendar = value.let {
        GregorianCalendar().also { calendar ->
            calendar.timeInMillis = value
        }
    }

    fun fromCalendarToTimestamp(timestamp: Calendar): Long = timestamp.timeInMillis

    fun canNotify(calendar: Calendar): Boolean {
        val currentCalendar = Calendar.getInstance()
        val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinutes = currentCalendar.get(Calendar.MINUTE)
        val reminderHour = calendar.get(Calendar.HOUR_OF_DAY)
        val reminderMinutes = calendar.get(Calendar.MINUTE)

        if (reminderHour >= currentHour) {
            if (reminderMinutes >= currentMinutes) {
                return true
            }
        }
        return false
    }
}