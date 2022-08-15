package br.com.vieirateam.paranote.service

import  android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import br.com.vieirateam.paranote.database.NoteDatabase
import br.com.vieirateam.paranote.util.CalendarUtil
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.entity.Backup as mBackup
import br.com.vieirateam.paranote.util.NotificationUtil
import ir.androidexception.roomdatabasebackupandrestore.Restore
import ir.androidexception.roomdatabasebackupandrestore.Backup

class BackupService: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.getBundleExtra(ConstantsUtil.BUNDLE) as Bundle
        val backup = bundle.getSerializable(ConstantsUtil.BACKUP) as mBackup
        val database = NoteDatabase.getDatabase()

        if (backup.doBackup) {
            Backup.Init()
                .database(database)
                .path(backup.path)
                .fileName(ConstantsUtil.DATABASE_FILE)
                .onWorkFinishListener { success, _ ->
                    if (success) { canNotify(backup) }
                }
                .execute()
        } else {
            Restore.Init()
                .database(database)
                .backupFilePath(backup.path + ConstantsUtil.DATABASE_FILE)
                .onWorkFinishListener { success, _ ->
                    if (success) { canNotify(backup) }
            }
            .execute()
        }
    }

    private fun canNotify(backup: mBackup) {
        if (CalendarUtil.canNotify(backup.calendar)) {
            NotificationUtil.build(backup.doBackup)
        }
    }
}