package br.com.vieirateam.paranote.util

import br.com.vieirateam.paranote.entity.Note
import com.google.gson.Gson

object SerializableUtil {

    fun serializable(value: Note): String = Gson().toJson(value, Note::class.java)

    fun deserializable(value: String): Note = Gson().fromJson(value, Note::class.java)
}