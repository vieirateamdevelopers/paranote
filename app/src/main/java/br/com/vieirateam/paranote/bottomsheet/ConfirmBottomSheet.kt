package br.com.vieirateam.paranote.bottomsheet

import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import kotlinx.android.synthetic.main.bottom_sheet_confirm.view.button_cancel
import kotlinx.android.synthetic.main.bottom_sheet_confirm.view.button_confirm

class ConfirmBottomSheet(context: AppCompatActivity, listener: Callback):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_confirm) {

    override fun build() {
        mView.button_cancel.setOnClickListener {
            mBottomSheetListener.setOnBottomSheetClickListener("cancel")
            hide()
        }

        mView.button_confirm.setOnClickListener {
            mBottomSheetListener.setOnBottomSheetClickListener("confirm")
            hide()
        }
    }
}