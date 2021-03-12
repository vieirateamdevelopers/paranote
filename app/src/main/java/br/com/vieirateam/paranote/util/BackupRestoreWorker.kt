package br.com.vieirateam.paranote.util

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.com.vieirateam.paranote.database.NoteDatabase
import ir.androidexception.roomdatabasebackupandrestore.Backup
import ir.androidexception.roomdatabasebackupandrestore.Restore

class BackupRestoreWorker(val context: Context, parameters: WorkerParameters): Worker(context, parameters) {

    override fun doWork(): Result {
        return try {
            val doBackup = inputData.getBoolean(ConstantsUtil.BACKUP_MODE, true)
            val path = inputData.getString(ConstantsUtil.PATH)
            val database = NoteDatabase.getDatabase()
            if (doBackup) {
                Backup.Init()
                    .database(database)
                    .path(path)
                    .fileName(ConstantsUtil.DATABASE_FILE)
                    .onWorkFinishListener { success, message ->
                        if (success) {
                            Log.d(ConstantsUtil.TAG, message)
                            NotificationUtil.build(doBackup)
                        }
                    }
                    .execute()
            } else {
                Restore.Init()
                    .database(database)
                    .backupFilePath(path + ConstantsUtil.DATABASE_FILE)
                    .onWorkFinishListener { success, message ->
                        if (success) {
                            Log.d(ConstantsUtil.TAG, message)
                            NotificationUtil.build(doBackup)
                        }
                    }
                    .execute()
            }
            Result.success()
        } catch (throwable: Throwable) {
            Log.d(ConstantsUtil.TAG, throwable.toString())
            Result.failure()
        }
    }
}