package br.com.vieirateam.paranote.extension

import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R

fun AppCompatActivity.configureDarkMode(darkMode: Boolean): Boolean {
    if (darkMode) {
        setTheme(R.style.DarkTheme)
    } else {
        setTheme(R.style.LightTheme)
    }
    return true
}