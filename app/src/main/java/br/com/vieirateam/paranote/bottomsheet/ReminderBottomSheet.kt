package br.com.vieirateam.paranote.bottomsheet

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import kotlinx.android.synthetic.main.bottom_sheet_reminder.view.bottom_sheet_reminder
import kotlinx.android.synthetic.main.bottom_sheet_reminder.view.switch_reminder

class ReminderBottomSheet(context: AppCompatActivity, listener: Callback):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_reminder) {

    @SuppressLint("ClickableViewAccessibility")
    override fun build() {
        mView.bottom_sheet_reminder.minimumHeight = mBottomSheetHeight
        mView.switch_reminder.setOnClickListener {
            listener.setOnBottomSheetClickListener("reminder")
        }
        mView.switch_reminder.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }
}