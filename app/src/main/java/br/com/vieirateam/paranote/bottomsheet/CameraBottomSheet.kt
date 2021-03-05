package br.com.vieirateam.paranote.bottomsheet

import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import kotlinx.android.synthetic.main.bottom_sheet_camera.view.bottom_sheet_camera
import kotlinx.android.synthetic.main.bottom_sheet_camera.view.floating_button_camera

class CameraBottomSheet(context: AppCompatActivity, listener: Callback):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_camera) {

    override fun build() {
        mView.bottom_sheet_camera.minimumHeight = mBottomSheetHeight
        mView.floating_button_camera.setOnClickListener {
            listener.setOnBottomSheetClickListener("camera")
            hide()
        }
    }
}