package br.com.vieirateam.paranote.extension

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.vieirateam.paranote.util.ConstantsUtil

fun resultPermission(requestCode: Int, grantResults: IntArray): Boolean {
    return when(requestCode) {
        ConstantsUtil.REQUEST_CODE_CAMERA -> (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ConstantsUtil.REQUEST_CODE_VOICE -> (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ConstantsUtil.REQUEST_CODE_READ_STORAGE -> (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ConstantsUtil.REQUEST_CODE_WRITE_STORAGE -> (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        else -> false
    }
}

fun Fragment.checkPermission(manifestPermission: String): Boolean {
    val permission = ContextCompat.checkSelfPermission(requireActivity(), manifestPermission)
    return permission == PackageManager.PERMISSION_GRANTED
}

fun Fragment.requestPermissionRationale(manifestPermission: String, requestCode: Int): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), manifestPermission)
}

fun Fragment.requestPermission(manifestPermission: String, requestCode: Int) {
    ActivityCompat.requestPermissions(requireActivity(), arrayOf(manifestPermission), requestCode)
}
