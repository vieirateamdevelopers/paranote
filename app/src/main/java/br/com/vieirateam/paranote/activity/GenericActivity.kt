package br.com.vieirateam.paranote.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.extension.configureDarkMode
import br.com.vieirateam.paranote.util.ToastUtil
import br.com.vieirateam.paranote.util.UserPreferenceUtil
import kotlinx.android.synthetic.main.adapter_main_toolbar.material_toolbar

abstract class GenericActivity(private val layoutID: Int?): AppCompatActivity() {

    protected var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        configureDarkMode(UserPreferenceUtil.darkMode)
        super.onCreate(savedInstanceState)
        try {
            if (layoutID != null) setContentView(layoutID)
            posCreate(savedInstanceState)
        } catch (exception: RuntimeException) {
            ToastUtil.show(this, getString(R.string.text_view_activity_error, exception.toString()))
            finish()
        }
    }

    protected fun setSupportActionBar() {
        setSupportActionBar(material_toolbar)
    }

    abstract fun posCreate(savedInstanceState: Bundle?)
}