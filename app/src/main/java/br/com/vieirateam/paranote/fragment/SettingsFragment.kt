package br.com.vieirateam.paranote.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Backup
import br.com.vieirateam.paranote.extension.*
import br.com.vieirateam.paranote.util.BottomSheet
import br.com.vieirateam.paranote.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_confirm.view.*
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.Calendar

class SettingsFragment: GenericFragment(R.layout.fragment_settings), BottomSheet.Callback, TimePickerDialog.OnTimeSetListener {

    private lateinit var backupOptions: Array<String>
    private lateinit var context: AppCompatActivity
    private lateinit var mBackup: Backup

    override fun posActivityCreated(savedInstanceState: Bundle?) {
        context = (activity as AppCompatActivity)
        backupOptions = context.resources.getStringArray(R.array.text_view_backup_automatic_options)
        configureFields()
        configureButtons()
        configureSwitchButton()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (resultPermission(requestCode, grantResults)) {
            when(requestCode) {
                ConstantsUtil.REQUEST_CODE_READ_STORAGE -> configureRestoreDatabase()
                ConstantsUtil.REQUEST_CODE_WRITE_STORAGE -> configureBackupDatabase()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        showBottom = null
    }

    private fun checkPermissionsReadStorage() {
        val manifestPermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val requestCode = ConstantsUtil.REQUEST_CODE_READ_STORAGE
        if (checkPermission(manifestPermission)) {
            configureRestoreDatabase()
        } else {
            if (requestPermissionRationale(manifestPermission, requestCode)) {
                configureBottomSheetPermission(manifestPermission, requestCode)
            } else {
                requestPermission(manifestPermission, requestCode)
            }
        }
    }

    private fun checkPermissionsWriteStorage() {
        val manifestPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val requestCode = ConstantsUtil.REQUEST_CODE_WRITE_STORAGE
        if (checkPermission(manifestPermission)) {
            configureBackupDatabase()
        } else {
            if (requestPermissionRationale(manifestPermission, requestCode)) {
                configureBottomSheetPermission(manifestPermission, requestCode)
            } else {
                requestPermission(manifestPermission, requestCode)
            }
        }
    }

    private fun configureBottomSheetPermission(manifestPermission: String, requestCode: Int) {
        showBottom = "permission"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_confirm, this)
        builder.build()
        mDialogView = builder.getBottomSheetView()

        mDialogView.text_view_confirm_title.text = getString(R.string.text_view_permission_storage_title)
        mDialogView.text_view_confirm_body.text = getString(R.string.text_view_permission_storage_body)
        mDialogView.image_view_confirm.setImageResource(R.drawable.ic_drawable_storage)

        mDialogView.button_cancel.setOnClickListener {
            showBottom = null
            builder.setHide()
        }

        mDialogView.button_confirm.setOnClickListener {
            requestPermission(manifestPermission, requestCode)
            showBottom = null
            builder.setHide()
        }
    }

    private fun configureTimePickerDialog() {
        if (UserPreferenceUtil.backupCalendar > 0) {
            val value = UserPreferenceUtil.backupCalendar
            mBackup.calendar = CalendarUtil.toTimestampFromCalendar(value)
        }
        val hourOfDay = mBackup.calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = mBackup.calendar.get(Calendar.MINUTE)
        TimePickerDialog(context, this, hourOfDay, minutes, true).apply {
            create()
            show()
            setOnCancelListener { dialog ->
                cancelAutomaticBackup()
                dialog.dismiss()
            }
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        mBackup.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        mBackup.calendar.set(Calendar.MINUTE, minute)
        mBackup.calendar.set(Calendar.SECOND, 0)
        mBackup.calendar.set(Calendar.MILLISECOND, 0)
        val value = CalendarUtil.fromCalendarToTimestamp(mBackup.calendar)
        UserPreferenceUtil.backupCalendar = value
        configureBackupOptionsDialog()
    }

    private fun configureBackupOptionsDialog() {
        AlertDialog.Builder(context).apply {
            setTitle(getString(R.string.text_view_options))
            setSingleChoiceItems(getArrayAdapter(), UserPreferenceUtil.backupOption) { dialog, which ->
                UserPreferenceUtil.backupOption = which
                configurePeriodicBackupDatabase()
                dialog.dismiss()
            }
            setNegativeButton(getString(R.string.menu_back)) { dialog, _ ->
                cancelAutomaticBackup()
                dialog.dismiss()
            }
            setCancelable(true)
            create()
            show()
        }
    }

    private fun configurePeriodicBackupDatabase() {
        if (UserPreferenceUtil.backupAutomatic) {
            cancelBackup()
            when(UserPreferenceUtil.backupOption) {
                0 -> {
                    mBackup.intervalMillis = mBackup.intervalMillis * 1
                    mBackup.requestCode = ConstantsUtil.REQUEST_CODE_BACKUP_DAILY
                    AlarmManagerUtil.create(context, mBundle, mBackup)
                }
                1 -> {
                    mBackup.intervalMillis = mBackup.intervalMillis * 7
                    mBackup.requestCode = ConstantsUtil.REQUEST_CODE_BACKUP_WEEKLY
                    AlarmManagerUtil.create(context, mBundle, mBackup)
                }
                2 -> {
                    mBackup.intervalMillis = mBackup.intervalMillis * 30
                    mBackup.requestCode = ConstantsUtil.REQUEST_CODE_BACKUP_MONTHLY
                    AlarmManagerUtil.create(context, mBundle, mBackup)
                }
            }
        }
    }

    private fun configureBackupDatabase() {
        val path = FileUtil.getAppPath()
        if (path == null) {
            ToastUtil.show(context, getString(R.string.text_view_backup_error))
        } else {
            cancelBackup()
            mBackup = Backup(path, doBackup = true, repeat = false, requestCode = ConstantsUtil.REQUEST_CODE_BACKUP)
            AlarmManagerUtil.create(context, mBundle, mBackup)
            configureBottomSheetDialog(getString(R.string.text_view_backup_doing), getString(R.string.text_view_backup_finished))
        }
    }

    private fun configureRestoreDatabase() {
        val path = FileUtil.getAppPath()
        if (path == null) {
            ToastUtil.show(context, getString(R.string.text_view_restore_error))
        } else {
            cancelBackup()
            mBackup = Backup(path, doBackup = false, repeat = false, requestCode = ConstantsUtil.REQUEST_CODE_RESTORE)
            AlarmManagerUtil.create(context, mBundle, mBackup)
            configureBottomSheetDialog(getString(R.string.text_view_restore_doing), getString(R.string.text_view_restore_finished))
        }
    }

    private fun configureFields() {
        card_view_automatic_backup.setOnClickListener {
            if (UserPreferenceUtil.backupAutomatic) {
                val path = FileUtil.getAppPath()
                if (path == null) {
                    ToastUtil.show(context, getString(R.string.text_view_backup_error))
                } else {
                    mBackup = Backup(path)
                    configureTimePickerDialog()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureSwitchButton() {
        switch_dark_mode.isChecked = UserPreferenceUtil.darkMode
        switch_automatic_backup.isChecked = UserPreferenceUtil.backupAutomatic

        switch_dark_mode.setOnClickListener {
            UserPreferenceUtil.darkMode = !UserPreferenceUtil.darkMode
            mMainActivity.configureDarkMode(UserPreferenceUtil.darkMode)
            mMainActivity.recreate()
        }

        switch_automatic_backup.setOnClickListener {
            UserPreferenceUtil.backupAutomatic = !UserPreferenceUtil.backupAutomatic
            if (UserPreferenceUtil.backupAutomatic) {
                val path = FileUtil.getAppPath()
                if (path == null) {
                    ToastUtil.show(context, getString(R.string.text_view_backup_error))
                } else {
                    mBackup = Backup(path)
                    configureTimePickerDialog()
                }
            } else {
                cancelBackup()
            }
        }

        switch_dark_mode.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                return@setOnTouchListener false
            }
            return@setOnTouchListener false
        }

        switch_automatic_backup.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                return@setOnTouchListener false
            }
            return@setOnTouchListener false
        }
    }

    private fun configureButtons() {
        button_backup.setOnClickListener {
            checkPermissionsWriteStorage()
        }

        button_restore.setOnClickListener {
            checkPermissionsReadStorage()
        }
    }

    private fun cancelBackup() {
        val intent = AlarmManagerUtil.getBackupService(context)
        AlarmManagerUtil.cancel(context, intent, ConstantsUtil.REQUEST_CODE_BACKUP)
        AlarmManagerUtil.cancel(context, intent, ConstantsUtil.REQUEST_CODE_RESTORE)
        AlarmManagerUtil.cancel(context, intent, ConstantsUtil.REQUEST_CODE_BACKUP_DAILY)
        AlarmManagerUtil.cancel(context, intent, ConstantsUtil.REQUEST_CODE_BACKUP_WEEKLY)
        AlarmManagerUtil.cancel(context, intent, ConstantsUtil.REQUEST_CODE_BACKUP_MONTHLY)
    }

    private fun cancelAutomaticBackup() {
        UserPreferenceUtil.backupAutomatic = !UserPreferenceUtil.backupAutomatic
        configureSwitchButton()
    }

    private fun configureBottomSheetDialog(title: String, message: String) {
        showBottom = "dialog"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_dialog, this).apply {
            setLock(false)
            build()
        }
        mDialogView = builder.getBottomSheetView()
        mDialogView.text_view_dialog_title.text = title
        Handler().postDelayed({
            builder.setHide()
            ToastUtil.show(context, message)
            onBackPressed()
        }, ConstantsUtil.DIALOG_DELAY)
    }

    @SuppressLint("PrivateResource")
    private fun getArrayAdapter(): ArrayAdapter<String> {
        val arrayAdapter = ArrayAdapter<String>(context, R.layout.select_dialog_singlechoice_material)
        for (i in backupOptions) {
            arrayAdapter.add(i)
        }
        return arrayAdapter
    }

    override fun addOnClickListener() {}

    override fun onStartTextRecognition(dialog: BottomSheetDialog) {}

    override fun onFinishTextRecognition() {}

    override fun onClickListener(view: View) {}
}