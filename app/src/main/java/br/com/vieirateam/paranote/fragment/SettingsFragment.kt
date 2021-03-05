package br.com.vieirateam.paranote.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.bottomsheet.BaseBottomSheet
import br.com.vieirateam.paranote.bottomsheet.DialogBottomSheet
import br.com.vieirateam.paranote.extension.configureDarkMode
import br.com.vieirateam.paranote.util.*
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.concurrent.TimeUnit

class SettingsFragment: GenericFragment(R.layout.fragment_settings), BaseBottomSheet.Callback {

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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            when(requestCode) {
                ConstantsUtil.REQUEST_CODE_WRITE_STORAGE -> {
                    configureBackupDatabase()
                }
                ConstantsUtil.REQUEST_CODE_READ_STORAGE -> {
                    configureRestoreDatabase()
                }
            }
        }
    }

    private fun checkPermissionsReadStorage() {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            configureRestoreDatabase()
        } else {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), ConstantsUtil.REQUEST_CODE_READ_STORAGE)
        }
    }

    private fun checkPermissionsWriteStorage() {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            configureBackupDatabase()
        } else {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), ConstantsUtil.REQUEST_CODE_WRITE_STORAGE)
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
        val path = FileUtil.getBackupPath()
        if (path == null) {
            ToastUtil.show(context, getString(R.string.text_view_backup_error))
        } else {
            val workManager = WorkManager.getInstance(context)
            val request = OneTimeWorkRequest.Builder(BackupRestoreWorker::class.java).apply {
                setInitialDelay(1, TimeUnit.SECONDS)
                setInputData(getInputData(true, path))
            }
            workManager.enqueue(request.build())
            configureBottomSheetDialog(true)
        }
    }

    private fun configureRestoreDatabase() {
        val path = FileUtil.getRestorePath()
        if (path == null) {
            ToastUtil.show(context, getString(R.string.text_view_restore_error))
        } else {
            val workManager = WorkManager.getInstance(context)
            val request = OneTimeWorkRequest.Builder(BackupRestoreWorker::class.java).apply {
                setInitialDelay(1, TimeUnit.SECONDS)
                setInputData(getInputData(false, path))
            }
            workManager.enqueue(request.build())
            configureBottomSheetDialog(false)
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

    private fun configureBottomSheetDialog(doBackup: Boolean) {
        showBottom = "dialog"
        mBottomSheetListener = DialogBottomSheet(doBackup, context, this)
        mBottomSheetListener.build()
    }

    private fun configureBackupOptionsDialog() {
        AlertDialog.Builder(context).apply {
            setTitle(getString(R.string.text_view_options))
            setSingleChoiceItems(getArrayAdapter(), UserPreferenceUtil.backupOption) { dialog, which ->
                UserPreferenceUtil.backupOption = which
                val path = FileUtil.getBackupPath()
                if (path == null) {
                    ToastUtil.show(context, getString(R.string.text_view_backup_error))
                } else {
                    configurePeriodicBackupDatabase(path)
                }
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

    override fun onBottomSheetBackPressed() {}

    override fun setOnBottomSheetClickListener(buttonID: String) {}
}