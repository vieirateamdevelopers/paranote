package br.com.vieirateam.paranote

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import br.com.vieirateam.paranote.viewmodel.NoteViewModel

class NoteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance : NoteApplication? = null
        lateinit var noteViewModel: NoteViewModel

        fun getInstance() : NoteApplication {
            requireNotNull(instance) { "AndroidManifest.xml error" }
            return instance as NoteApplication
        }

        fun getViewModel() {
            val context = instance as Application
            noteViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(context).create(NoteViewModel::class.java)
        }
    }
}