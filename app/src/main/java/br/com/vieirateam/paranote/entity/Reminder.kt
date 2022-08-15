package br.com.vieirateam.paranote.entity

import androidx.room.ColumnInfo
import java.io.Serializable
import java.util.Calendar

data class Reminder(
    @ColumnInfo(name = "reminder_date_time")
    var dateTime: Calendar = Calendar.getInstance(),
    @ColumnInfo(name = "reminder_checked")
    var isChecked: Boolean = false,
    @ColumnInfo(name = "reminder_advance")
    var advance: Long = 0,
    @ColumnInfo(name = "reminder_repeat")
    var repeat: Long = 0
): Serializable