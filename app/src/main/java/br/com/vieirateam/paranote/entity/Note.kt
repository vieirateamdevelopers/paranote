package br.com.vieirateam.paranote.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "note_id")
    var id: Long = 0,
    @ColumnInfo(name = "note_title")
    var title: String = "",
    @ColumnInfo(name = "note_body")
    var body: String = "",
    @ColumnInfo(name = "note_color")
    var color: Int = 1,
    @ColumnInfo(name = "note_favorite")
    var favorite: Boolean = false,
    @ColumnInfo(name = "note_archived")
    var archived: Boolean = false,
    @Embedded(prefix = "note_")
    var reminder: Reminder = Reminder()
): Serializable
