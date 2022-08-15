package br.com.vieirateam.paranote.util

import android.content.SharedPreferences
import br.com.vieirateam.paranote.NoteApplication

object UserPreferenceUtil {

    private fun getSharedPreferences(): SharedPreferences {
        val context = NoteApplication.getInstance().applicationContext
        return context.getSharedPreferences(ConstantsUtil.PREF_ID, 0)
    }

    private fun setGridModePreference(value: Boolean) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putBoolean(ConstantsUtil.GRID_MODE, value)
        editor.apply()
    }

    private fun getGridModePreference(): Boolean {
        val preferences = getSharedPreferences()
        return preferences.getBoolean(ConstantsUtil.GRID_MODE, true)
    }

    private fun setDarkModePreference(value: Boolean) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putBoolean(ConstantsUtil.DARK_MODE, value)
        editor.apply()
    }

    private fun getDarkModePreference(): Boolean {
        val preferences = getSharedPreferences()
        return preferences.getBoolean(ConstantsUtil.DARK_MODE, false)
    }

    private fun setBackupModePreference(value: Boolean) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putBoolean(ConstantsUtil.BACKUP_MODE, value)
        editor.apply()
    }

    private fun getBackupModePreference(): Boolean {
        val preferences = getSharedPreferences()
        return preferences.getBoolean(ConstantsUtil.BACKUP_MODE, false)
    }

    private fun setBackupOptionModePreference(value: Int) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putInt(ConstantsUtil.BACKUP_OPTION_MODE, value)
        editor.apply()
    }

    private fun getBackupCalendarPreference(): Long {
        val preferences = getSharedPreferences()
        return preferences.getLong(ConstantsUtil.BACKUP_CALENDAR, 0)
    }

    private fun setBackupCalendarPreference(value: Long) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putLong(ConstantsUtil.BACKUP_CALENDAR, value)
        editor.apply()
    }

    private fun getBackupOptionModePreference(): Int {
        val preferences = getSharedPreferences()
        return preferences.getInt(ConstantsUtil.BACKUP_OPTION_MODE, 0)
    }

    private fun setNoteIDPreference(value: Long) {
        val preferences = getSharedPreferences()
        val editor = preferences.edit()
        editor.putLong(ConstantsUtil.NOTIFICATION, value)
        editor.apply()
    }

    private fun getNoteIDPreference(): Long {
        val preferences = getSharedPreferences()
        return preferences.getLong(ConstantsUtil.NOTIFICATION, 0)
    }

    var backupAutomatic
        get() = getBackupModePreference()
        set(value) = setBackupModePreference(value)

    var backupCalendar
        get() = getBackupCalendarPreference()
        set(value) = setBackupCalendarPreference(value)

    var backupOption
        get() = getBackupOptionModePreference()
        set(value) = setBackupOptionModePreference(value)

    var darkMode
        get() = getDarkModePreference()
        set(value) = setDarkModePreference(value)

    var gridMode
        get() = getGridModePreference()
        set(value) = setGridModePreference(value)

    var noteID
        get() = getNoteIDPreference()
        set(value) = setNoteIDPreference(value)
}