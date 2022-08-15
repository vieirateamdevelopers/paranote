package br.com.vieirateam.paranote.extension

import android.util.Log
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.vieirateam.paranote.util.ConstantsUtil

fun AppCompatActivity.addFragment(@IdRes layoutId: Int, fragment: Fragment): Boolean {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    val attachedFragment = supportFragmentManager.findFragmentById(layoutId)

    if (attachedFragment != null) fragmentTransaction.remove(attachedFragment)
    fragmentTransaction.add(layoutId, fragment)

    try {
        fragmentTransaction.commit()
        return true
    } catch (exception: IllegalStateException) {
        Log.d(ConstantsUtil.TAG, exception.toString())
    }
    return false
}