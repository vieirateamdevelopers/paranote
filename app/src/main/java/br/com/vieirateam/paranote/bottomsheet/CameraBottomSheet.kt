package br.com.vieirateam.paranote.bottomsheet

import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.util.CameraUtil
import br.com.vieirateam.paranote.util.ConstantsUtil
import kotlinx.android.synthetic.main.bottom_sheet_camera.view.*
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.*

class CameraBottomSheet(private val context: AppCompatActivity, listener: Callback):
    BaseBottomSheet(context, listener, R.layout.bottom_sheet_camera){

    private lateinit var mCameraUtil: CameraUtil

    override fun build() {
        mView.bottom_sheet_camera.minimumHeight = mBottomSheetHeight
        mView.image_view_base_send.setImageResource(R.drawable.ic_drawable_flash_off)
        mCameraUtil = CameraUtil(context, mView)
        mCameraUtil.setCameraBottomSheet(this)
        mCameraUtil.show()

        mView.floating_button_camera.setOnClickListener {
            mCameraUtil.takePhoto()
            Handler().postDelayed({
                mCameraUtil.shutdownFlash()
                listener.setOnBottomSheetClickListener("camera")
                hide()
            }, ConstantsUtil.INTRO_DELAY)
        }

        mView.image_view_base_send.setOnClickListener {
            mCameraUtil.setFlash()
        }

        val backButton = mView.findViewById<AppCompatImageView>(R.id.image_view_base_back)
        backButton.setOnClickListener {
            mCameraUtil.shutdownFlash()
            hide()
            back()
        }
    }

    fun getCameraUtil() = mCameraUtil
}