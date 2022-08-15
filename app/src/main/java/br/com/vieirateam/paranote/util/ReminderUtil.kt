package br.com.vieirateam.paranote.util

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.entity.Reminder
import kotlinx.android.synthetic.main.bottom_sheet_reminder.view.*
import java.util.Calendar

class ReminderUtil(note: Note, private val view: View): DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var positionAdvance: Int = 0
    private var positionRepeat: Int = 0
    private var context = view.context
    private var reminder: Reminder = note.reminder
    private val reminderAdvance = context.resources.getStringArray(R.array.text_view_advance_options)
    private val reminderRepeat = context.resources.getStringArray(R.array.text_view_repeat_options)

    init {
        configureFields()
    }

    private fun configureFields() {
        view.card_view_date.setOnClickListener {
            if (reminder.isChecked) {
                configureDatePickerDialog()
            }
        }

        view.card_view_hour.setOnClickListener {
            if (reminder.isChecked) {
                configureTimePickerDialog()
            }
        }

        view.card_view_advance.setOnClickListener {
            if (reminder.isChecked) {
                configureReminderAdvanceDialog()
            }
        }

        view.card_view_repeat.setOnClickListener {
            if (reminder.isChecked) {
                configureReminderRepeatDialog()
            }
        }
        updatePositions()
        updateReminder()
    }

    private fun configureDatePickerDialog() {
        val year = reminder.dateTime.get(Calendar.YEAR)
        val month = reminder.dateTime.get(Calendar.MONTH)
        val dayOfMonth = reminder.dateTime.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(context, this, year, month, dayOfMonth).apply {
            datePicker.minDate = Calendar.getInstance().timeInMillis
            create()
            show()
        }
    }

    private fun configureTimePickerDialog() {
        val hourOfDay = reminder.dateTime.get(Calendar.HOUR_OF_DAY)
        val minutes = reminder.dateTime.get(Calendar.MINUTE)
        TimePickerDialog(context, this, hourOfDay, minutes, true).apply {
            create()
            show()
        }
    }

    private fun configureReminderAdvanceDialog() {
        AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.text_view_reminder_advance))
            setSingleChoiceItems(getArrayAdapterAdvance(), positionAdvance) { dialog, which ->
                reminder.advance = ConstantsUtil.ARRAY_SECONDS[which]
                positionAdvance = which
                updateReminder()
                dialog.dismiss()
            }
            setCancelable(true)
            create()
            show()
        }
    }

    private fun configureReminderRepeatDialog() {
        AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.text_view_reminder_repeat))
            setSingleChoiceItems(getArrayAdapterRepeat(), positionRepeat) { dialog, which ->
                reminder.repeat = ConstantsUtil.ARRAY_REPEAT[which]
                positionRepeat = which
                updateReminder()
                dialog.dismiss()
            }
            setCancelable(true)
            create()
            show()
        }
    }

    @SuppressLint("PrivateResource")
    private fun getArrayAdapterAdvance(): ArrayAdapter<String> {
        val arrayAdapter = ArrayAdapter<String>(context, R.layout.select_dialog_singlechoice_material)
        for (i in reminderAdvance) {
            arrayAdapter.add(i)
        }
        return arrayAdapter
    }

    @SuppressLint("PrivateResource")
    private fun getArrayAdapterRepeat(): ArrayAdapter<String> {
        val arrayAdapter = ArrayAdapter<String>(context, R.layout.select_dialog_singlechoice_material)
        for (i in reminderRepeat) {
            arrayAdapter.add(i)
        }
        return arrayAdapter
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        reminder.dateTime.set(Calendar.YEAR, year)
        reminder.dateTime.set(Calendar.MONTH, month)
        reminder.dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateReminder()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        reminder.dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        reminder.dateTime.set(Calendar.MINUTE, minute)
        reminder.dateTime.set(Calendar.SECOND, 0)
        reminder.dateTime.set(Calendar.MILLISECOND, 0)
        updateReminder()
    }

    private fun updatePositions() {
        for((i, position) in ConstantsUtil.ARRAY_SECONDS.withIndex()) {
            if (reminder.advance == position) {
                positionAdvance = i
            }
        }
        for((i, position) in ConstantsUtil.ARRAY_REPEAT.withIndex()) {
            if (reminder.repeat == position) {
                positionRepeat = i
            }
        }
    }

    private fun updateReminder() {
        val date = DateFormatUtil.format(reminder.dateTime.time, true)
        val hour = DateFormatUtil.format(reminder.dateTime.time, false)
        view.text_view_date.text = date
        view.text_view_hour.text = hour
        view.switch_reminder.isChecked = reminder.isChecked
        view.text_view_advance.text = reminderAdvance[positionAdvance]
        view.text_view_repeat.text = reminderRepeat[positionRepeat]
    }

    fun disableFields(reminder: Reminder) {
        this.reminder = reminder
        view.card_view_date.isEnabled = reminder.isChecked
        view.card_view_hour.isEnabled = reminder.isChecked
        view.card_view_advance.isEnabled = reminder.isChecked
        view.card_view_repeat.isEnabled = reminder.isChecked
    }

    fun getReminder() = reminder
}