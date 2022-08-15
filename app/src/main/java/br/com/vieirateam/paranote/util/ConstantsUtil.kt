package br.com.vieirateam.paranote.util

object ConstantsUtil {

    const val TAG = "ParanoteApplication"
    const val GRID_MODE = "grid_mode"
    const val DARK_MODE = "dark_mode"
    const val BACKUP = "backup"
    const val BACKUP_MODE = "backup_mode"
    const val BACKUP_CALENDAR = "backup_calendar"
    const val BACKUP_OPTION_MODE = "backup_option_mode"
    const val DATABASE_NAME = "paranote.db"
    const val DATABASE_FILE = "database.json"
    const val NOTIFICATION = "notification"
    const val FRAGMENT = "fragment"
    const val BUNDLE = "bundle"
    const val TITLE = "title"
    const val BODY = "body"
    const val ITEM = "item"
    const val SAVE = "save"
    const val HOME = "home"
    const val PATH = "path"
    const val VOICE = "voice"
    const val EDIT_TITLE = "edit_title"
    const val BOTTOM_SHEET = "bottom_sheet"
    const val INTRO_DELAY: Long = 1000
    const val DIALOG_DELAY: Long = 10000
    const val TOUCH_TOLERANCE: Int = 4
    const val STROKE_WIDTH: Float = 5f
    const val REQUEST_CODE_VOICE: Int = 100
    const val REQUEST_CODE_CAMERA: Int = 101
    const val REQUEST_CODE_READ_STORAGE: Int = 102
    const val REQUEST_CODE_WRITE_STORAGE: Int = 103
    const val REQUEST_CODE_BACKUP: Int = 200
    const val REQUEST_CODE_RESTORE: Int = 201
    const val REQUEST_CODE_BACKUP_DAILY: Int = 202
    const val REQUEST_CODE_BACKUP_WEEKLY: Int = 203
    const val REQUEST_CODE_BACKUP_MONTHLY: Int = 204
    const val REQUEST_CODE_PENDING_TEMP: Int = 9999999

    const val MILLISECONDS: Long = 86400000
    val ARRAY_SECONDS: Array<Long> = arrayOf(0, 300000, 600000, 1800000, 3600000)
    val ARRAY_REPEAT: Array<Long> = arrayOf(0, 1, 7, 30)

    const val PREF_ID = "br.com.vieirateam.paranote"
    const val KEY_GROUP = "br.com.vieirateam.paranote.KEY_GROUP"
    const val CHANNEL_ID = "br.com.vieirateam.paranote.CHANNEL_ID"

}