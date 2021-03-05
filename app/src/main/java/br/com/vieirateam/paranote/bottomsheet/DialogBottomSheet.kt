package br.com.vieirateam.paranote.bottomsheet

import android.annotation.SuppressLint
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.util.ToastUtil
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.text_view_dialog_title

class DialogBottomSheet(private val doBackup: Boolean, private val context: AppCompatActivity,listener: Callback):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_dialog) {

    @SuppressLint("SetTextI18n")
    override fun build() {
        mBottomSheetDialog.setCancelable(false)
        var title = ""
        var message = ""
        if (doBackup) {
            title = context.getString(R.string.text_view_backup_doing)
            message = context.getString(R.string.text_view_backup_finished)
        } else {
            title = context.getString(R.string.text_view_restore_doing)
            message = context.getString(R.string.text_view_restore_finished)
        }
        mView.text_view_dialog_title.text = title
        Handler().postDelayed({
            hide()
            ToastUtil.show(context, message)
        }, ConstantsUtil.DIALOG_DELAY)
    }
}