package br.com.vieirateam.paranote.util

import android.os.Environment
import java.io.File

object FileUtil {

    fun getBackupPath(): String? {
        val path = Environment.getExternalStorageDirectory()
        val folder = File("$path"+ (File.separator.toString() + "Paranote"))
        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        }
        return if (success) {
            "${folder.absolutePath}/"
        } else {
            null
        }
    }

    fun getRestorePath(): String? {
        val file = File("/storage/emulated/0/Paranote/database.json")
        return if (file.exists()) {
            file.absolutePath
        } else {
            null
        }
    }

}