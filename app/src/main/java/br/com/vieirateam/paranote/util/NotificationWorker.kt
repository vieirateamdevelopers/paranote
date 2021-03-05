package br.com.vieirateam.paranote.util

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.com.vieirateam.paranote.activity.MainActivity
import br.com.vieirateam.paranote.entity.Note

class NotificationWorker(val context: Context, parameters: WorkerParameters): Worker(context, parameters) {

    override fun doWork(): Result {
        return try {
            val json = inputData.getString(ConstantsUtil.ITEM).toString()
            val note = SerializableUtil.deserializable(json)
            createNotification(note)
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }

    private fun createNotification(note: Note) {
        val intent = Intent(context, MainActivity::class.java)
        NotificationUtil.build(intent, note)
    }
}