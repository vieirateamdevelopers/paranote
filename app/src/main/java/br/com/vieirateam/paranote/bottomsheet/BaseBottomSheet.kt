package br.com.vieirateam.paranote.bottomsheet

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import br.com.vieirateam.paranote.R
import kotlinx.android.synthetic.main.bottom_sheet_options.view.floating_button_option_4
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.text_view_base_title
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.material_bottom_toolbar
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_clear
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_redo
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_send
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_undo

abstract class BaseBottomSheet(context: AppCompatActivity, protected val listener: Callback, layoutID: Int):
    GenericBottomSheet(context, layoutID) {

    protected var mBottomSheetText: String? = null
    protected var mBottomSheetColor: Int? = null
    protected val mBottomSheetListener: Callback = listener
    protected val mBottomSheetHeight = context.resources.getInteger(R.integer.bottom_sheet_height)

    init {
        if( layoutID != R.layout.bottom_sheet_options &&
            layoutID != R.layout.bottom_sheet_confirm &&
            layoutID != R.layout.bottom_sheet_dialog) {
            val backButton = mView.findViewById<AppCompatImageView>(R.id.image_view_base_back)
            backButton.setOnClickListener {
                hide()
                back()
            }
        }

        mBottomSheetDialog.setOnCancelListener {
            listener.onBottomSheetBackPressed()
        }
    }

    override fun back() {
        listener.onBottomSheetBackPressed()
    }

    fun setTitle(title: String, removeView: Boolean?) {
        mView.text_view_base_title.text = title
        if (removeView != null) {
            if (removeView) {
                mView.material_bottom_toolbar.removeView(mView.image_view_base_undo)
                mView.material_bottom_toolbar.removeView(mView.image_view_base_redo)
                mView.material_bottom_toolbar.removeView(mView.image_view_base_clear)
                mView.material_bottom_toolbar.removeView(mView.image_view_base_send)
            } else {
                mView.material_bottom_toolbar.removeView(mView.image_view_base_undo)
                mView.material_bottom_toolbar.removeView(mView.image_view_base_redo)
            }
        }
    }

    fun setFavoriteNote(favorite: Boolean) {
        if (favorite) {
            mView.floating_button_option_4.setImageResource(R.drawable.ic_drawable_favorite_2)
        } else {
            mView.floating_button_option_4.setImageResource(R.drawable.ic_drawable_favorite_1)
        }
    }

    fun getBottomSheetText(): String? = mBottomSheetText

    fun getBottomSheetColor(): Int? = mBottomSheetColor

    fun getBottomSheetView(): View = mView

    abstract fun build()

    interface Callback {
        fun onBottomSheetBackPressed()
        fun setOnBottomSheetClickListener(buttonID: String)
    }
}