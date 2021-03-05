package br.com.vieirateam.paranote.bottomsheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.util.KeyboardUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class GenericBottomSheet(context: AppCompatActivity, layoutID: Int):
    BottomSheetBehavior.BottomSheetCallback() {

    private var mBottomSheetBehavior: BottomSheetBehavior<View>
    protected val mBottomSheetDialog = BottomSheetDialog(context)
    protected var mView: View

    init {
        val viewGroup = (context.window.decorView.rootView) as ViewGroup
        mView = LayoutInflater.from(context).inflate(layoutID, viewGroup, false)
        mBottomSheetDialog.setContentView(mView)
        mBottomSheetBehavior = BottomSheetBehavior.from((mView.parent) as View)
        mBottomSheetDialog.setOnShowListener { mBottomSheetBehavior.setPeekHeight(mView.height) }
        addBottomSheetCallback()
        mBottomSheetDialog.show()
    }

    private fun addBottomSheetCallback() {
        mBottomSheetBehavior.addBottomSheetCallback(this)
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            hide()
            back()
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        if (slideOffset < -0.50f) {
            hide()
            back()
        }
    }

    protected fun hide() {
        KeyboardUtil.hide(mView)
        mBottomSheetDialog.dismiss()
        mBottomSheetBehavior.removeBottomSheetCallback(this)
    }

    fun lockBottomSheet(lock: Boolean) {
        mBottomSheetDialog.setCancelable(lock)
    }

    abstract fun back()
}