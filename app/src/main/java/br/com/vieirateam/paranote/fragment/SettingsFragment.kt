package br.com.vieirateam.paranote.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.extension.*
import br.com.vieirateam.paranote.util.BottomSheet
import br.com.vieirateam.paranote.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_confirm.view.*
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.concurrent.TimeUnit

class SettingsFragment: GenericFragment(R.layout.fragment_settings), BottomSheet.Callback {

    private lateinit var backupOptions: Array<String>
    private lateinit var context: AppCompatActivity

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
    private fun configurePeriodicBackupDatabase(path: String) {
        if (UserPreferenceUtil.backupAutomatic) {
            Log.d(ConstantsUtil.TAG, UserPreferenceUtil.backupOption.toString())
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag("daily")
            workManager.cancelAllWorkByTag("weekly")
            workManager.cancelAllWorkByTag("monthly")
            when(UserPreferenceUtil.backupOption) {
                0 -> {
                    val request = PeriodicWorkRequest.Builder(BackupRestoreWorker::class.java, 1, TimeUnit.DAYS)
                    request.addTag("daily")
                    request.setInputData(getInputData(true, path))
                    workManager.enqueue(request.build())
                }
                1 -> {
                    val request = PeriodicWorkRequest.Builder(BackupRestoreWorker::class.java, 7, TimeUnit.DAYS)
                    request.addTag("weekly")
                    request.setInputData(getInputData(true, path))
                    workManager.enqueue(request.build())
                }
                2 -> {
                    val request = PeriodicWorkRequest.Builder(BackupRestoreWorker::class.java, 30, TimeUnit.DAYS)
                    request.addTag("monthly")
                    request.setInputData(getInputData(true, path))
                    workManager.enqueue(request.build())
                }
            }
        }
    }

    private fun configureBackupDatabase() {
        val path = FileUtil.getAppPath()
        if (path == null) {
            ToastUtil.show(context, getString(R.string.text_view_backup_error))
        } else {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag("backup")
            workManager.cancelAllWorkByTag("restore")
            val request = OneTimeWorkRequest.Builder(BackupRestoreWorker::class.java).apply {
                addTag("backup")
                setInitialDelay(1, TimeUnit.SECONDS)
                setInputData(getInputData(true, path))
            }
            workManager.enqueue(request.build())
            configureBottomSheetDialog(getString(R.string.text_view_backup_doing), getString(R.string.text_view_backup_finished))
        }
    }

    private fun configureRestoreDatabase() {
        val path = FileUtil.getAppPath()
        if (path == null) {
            ToastUtil.show(context, getString(R.string.text_view_restore_error))
        } else {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWorkByTag("backup")
            workManager.cancelAllWorkByTag("restore")
            val request = OneTimeWorkRequest.Builder(BackupRestoreWorker::class.java).apply {
                addTag("restore")
                setInitialDelay(1, TimeUnit.SECONDS)
                setInputData(getInputData(false, path))
            }
            workManager.enqueue(request.build())
            configureBottomSheetDialog(getString(R.string.text_view_restore_doing), getString(R.string.text_view_restore_finished))
        }
    }

    private fun configureFields() {
        card_view_automatic_backup.setOnClickListener {
            if (UserPreferenceUtil.backupAutomatic) {
                configureBackupOptionsDialog()
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
                configureBackupOptionsDialog()
            } else {
                val workManager = WorkManager.getInstance(context)
                workManager.cancelAllWorkByTag("daily")
                workManager.cancelAllWorkByTag("weekly")
                workManager.cancelAllWorkByTag("monthly")
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

    private fun configureBackupOptionsDialog() {
        AlertDialog.Builder(context).apply {
            setTitle(getString(R.string.text_view_options))
            setSingleChoiceItems(getArrayAdapter(), UserPreferenceUtil.backupOption) { dialog, which ->
                UserPreferenceUtil.backupOption = which
                val path = FileUtil.getAppPath()
                if (path == null) {
                    ToastUtil.show(context, getString(R.string.text_view_backup_error))
                } else {
                    configurePeriodicBackupDatabase(path)
                }
                dialog.dismiss()
            }
            setNegativeButton(getString(R.string.menu_back)) { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(true)
            create()
            show()
        }
    }

    @SuppressLint("PrivateResource")
    private fun getArrayAdapter(): ArrayAdapter<String> {
        val arrayAdapter = ArrayAdapter<String>(context, R.layout.select_dialog_singlechoice_material)
        for (i in backupOptions) {
            arrayAdapter.add(i)
        }
        return arrayAdapter
    }

    private fun getInputData(doBackup: Boolean, path: String): Data {
        return Data.Builder()
            .putBoolean(ConstantsUtil.BACKUP_MODE, doBackup)
            .putString(ConstantsUtil.PATH, path)
            .build()
    }

    override fun addOnClickListener() {}

    override fun onStartTextRecognition(dialog: BottomSheetDialog) {}

    override fun onFinishTextRecognition() {}

    override fun onClickListener(view: View) {}
}