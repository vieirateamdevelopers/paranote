package br.com.vieirateam.paranote.util

import android.os.Environment
import java.io.File

object FileUtil {

    fun getAppPath(): String? {
        val path = Environment.getExternalStorageDirectory()
        val folder = File("$path" + (File.separator.toString() + "Paranote"))
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

    fun getImageCapturePath(): String? {
        val file = getAppPath()
        if (file != null) {
            val folder = File("$file" + (File.separator.toString() + "Images"))
            if (!folder.exists()) {
                folder.mkdirs()
            }
            return "${folder.absolutePath}/"
        }
        return null
    }
}