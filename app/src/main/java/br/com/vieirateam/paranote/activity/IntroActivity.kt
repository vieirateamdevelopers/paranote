package br.com.vieirateam.paranote.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.util.ConstantsUtil

class IntroActivity: GenericActivity(null) {

    override fun posCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val parameters = WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.setFlags(parameters, parameters)
        setContentView(R.layout.activity_intro)
        Handler().postDelayed({
            configureIntent()
        }, ConstantsUtil.INTRO_DELAY)
    }

    private fun configureIntent() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}