package br.com.vieirateam.paranote.entity

import br.com.vieirateam.paranote.util.ConstantsUtil
import java.io.Serializable
import java.util.Calendar

data class Backup(
    val path: String,
    var requestCode: Int = 0,
    val doBackup: Boolean = true,
    val repeat: Boolean = true,
    var calendar: Calendar = Calendar.getInstance(),
    var intervalMillis: Long = ConstantsUtil.MILLISECONDS
): Serializable