package br.com.vieirateam.paranote.util

import androidx.room.TypeConverter
import java.util.Calendar
import java.util.GregorianCalendar

class ConvertersUtil {
    @TypeConverter
    fun toTimestampFromCalendar(value: Long): Calendar = value.let {
        GregorianCalendar().also { calendar ->
            calendar.timeInMillis = value
        }
    }

    @TypeConverter
    fun fromCalendarToTimestamp(timestamp: Calendar): Long = timestamp.timeInMillis
}
