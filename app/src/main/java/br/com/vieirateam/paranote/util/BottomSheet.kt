package br.com.vieirateam.paranote.util

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Note
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_options.view.*
import kotlinx.android.synthetic.main.bottom_sheet_text.view.*
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.*

class BottomSheet(private val context: AppCompatActivity,
                  private val layoutID: Int,
                  private val listener: Callback): BottomSheetBehavior.BottomSheetCallback() {

    private var mBottomSheetView: View
    private var mBottomSheetDialog = BottomSheetDialog(context)
    private var mBottomSheetBehavior: BottomSheetBehavior<View>

    init {
        val viewGroup = (context.window.decorView.rootView) as ViewGroup
        mBottomSheetView = LayoutInflater.from(context).inflate(layoutID, viewGroup, false)
        mBottomSheetDialog.setContentView(mBottomSheetView)
        mBottomSheetBehavior = BottomSheetBehavior.from((mBottomSheetView.parent) as View)
        mBottomSheetDialog.setOnShowListener{ mBottomSheetBehavior.setPeekHeight(mBottomSheetView.height) }
        mBottomSheetBehavior.addBottomSheetCallback(this)
        mBottomSheetDialog.setOnCancelListener { listener.onBackPressed() }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            listener.onBackPressed()
            setHide()
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        if (slideOffset < -0.50f) {
            listener.onBackPressed()
            setHide()
        }
    }

    private fun setTitle(title: String) {
        mBottomSheetView.text_view_title.text = title
        mBottomSheetView.image_view_back.setOnClickListener {
            listener.onBackPressed()
            setHide()
        }
    }

    private fun setInflateMenus() {
        when(layoutID) {
            R.layout.bottom_sheet_camera -> {
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_redo)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_undo)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_clear)
                mBottomSheetView.image_view_send.setImageResource(R.drawable.ic_drawable_flash_on)
            }
            R.layout.bottom_sheet_image -> {
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_redo)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_undo)
                mBottomSheetView.image_view_clear.setImageResource(R.drawable.ic_drawable_crop_rotate)
            }
            R.layout.bottom_sheet_reminder -> {
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_redo)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_undo)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_clear)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_send)
            }
            R.layout.bottom_sheet_color_light -> {
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_redo)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_undo)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_clear)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_send)
            }
            R.layout.bottom_sheet_color_dark -> {
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_redo)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_undo)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_clear)
                mBottomSheetView.material_bottom_toolbar.removeView(mBottomSheetView.image_view_send)
            }
        }
        setToolbarListeners()
    }

    private fun setToolbarListeners() {
        when(layoutID) {
            R.layout.bottom_sheet_camera -> {
                mBottomSheetView.image_view_send.setOnClickListener {
                    listener.onClickListener(it)
                }
            }
            R.layout.bottom_sheet_draw -> {
                mBottomSheetView.image_view_undo.setOnClickListener {
                    listener.onClickListener(it)
                }
                mBottomSheetView.image_view_redo.setOnClickListener {
                    listener.onClickListener(it)
                }
                mBottomSheetView.image_view_clear.setOnClickListener {
                    listener.onClickListener(it)
                }
                mBottomSheetView.image_view_send.setOnClickListener {
                    listener.onStartTextRecognition(mBottomSheetDialog)
                }
            }
            R.layout.bottom_sheet_image -> {
                mBottomSheetView.image_view_clear.setOnClickListener {
                    listener.onClickListener(it)
                }
                mBottomSheetView.image_view_send.setOnClickListener {
                    listener.onStartTextRecognition(mBottomSheetDialog)
                }
            }
            R.layout.bottom_sheet_text -> {
                mBottomSheetView.image_view_undo.setOnClickListener {
                    listener.onClickListener(it)
                }
                mBottomSheetView.image_view_redo.setOnClickListener {
                    listener.onClickListener(it)
                }
                mBottomSheetView.image_view_clear.setOnClickListener {
                    listener.onClickListener(it)
                }
                mBottomSheetView.image_view_send.setOnClickListener {
                    listener.onClickListener(it)
                    setHide()
                }
            }
        }
    }

    private fun setFavorite(note: Note) {
        val resource = if (note.favorite) {
            R.drawable.ic_drawable_favorite_1
        } else {
            R.drawable.ic_drawable_favorite_2
        }
        mBottomSheetView.floating_button_option_4.setImageResource(resource)
    }

    private fun setVisibleButtons(note: Note?) {
        if (note == null) {
            mBottomSheetView.button_reminder_base.visibility = View.INVISIBLE
            mBottomSheetView.linear_layout_base.removeView(mBottomSheetView.text_input_base_body)
            mBottomSheetView.floating_button_base.hide()
        } else {
            mBottomSheetView.button_reminder_base.visibility = View.VISIBLE
            ColorsUtil.setBackgroundColor(note, mBottomSheetView)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnScrollChangeListener() {
        mBottomSheetView.text_input_base_body.minHeight = context.window.decorView.height
        try {
            mBottomSheetView.text_input_base_body.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                setLock(false)
                if (scrollY > oldScrollY) {
                    mBottomSheetView.floating_button_base.hide()
                } else {
                    mBottomSheetView.floating_button_base.show()
                }
            }
        } catch (exception: NullPointerException) {
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }

    private fun setOptions(home: Boolean) {
        if (home) {
            mBottomSheetView.floating_button_option_1.setImageResource(R.drawable.ic_drawable_camera)
            mBottomSheetView.floating_button_option_2.setImageResource(R.drawable.ic_drawable_draw)
            mBottomSheetView.floating_button_option_3.setImageResource(R.drawable.ic_drawable_image)
            mBottomSheetView.floating_button_option_4.setImageResource(R.drawable.ic_drawable_voice_white)
            mBottomSheetView.floating_button_option_5.setImageResource(R.drawable.ic_drawable_keyboard)

            mBottomSheetView.text_view_option_1.text = context.getString(R.string.menu_camera)
            mBottomSheetView.text_view_option_2.text = context.getString(R.string.menu_draw)
            mBottomSheetView.text_view_option_3.text = context.getString(R.string.menu_upload)
            mBottomSheetView.text_view_option_4.text = context.getString(R.string.menu_voice)
            mBottomSheetView.text_view_option_5.text = context.getString(R.string.menu_keyboard)
        } else {
            mBottomSheetView.floating_button_option_1.setImageResource(R.drawable.ic_drawable_share)
            mBottomSheetView.floating_button_option_2.setImageResource(R.drawable.ic_drawable_copy)
            mBottomSheetView.floating_button_option_3.setImageResource(R.drawable.ic_drawable_color)
            mBottomSheetView.floating_button_option_5.setImageResource(R.drawable.ic_drawable_reminder)

            mBottomSheetView.text_view_option_1.text = context.getString(R.string.menu_share)
            mBottomSheetView.text_view_option_2.text = context.getString(R.string.menu_copy)
            mBottomSheetView.text_view_option_3.text = context.getString(R.string.menu_color)
            mBottomSheetView.text_view_option_4.text = context.getString(R.string.menu_favorite)
            mBottomSheetView.text_view_option_5.text = context.getString(R.string.menu_reminder)
        }
    }

    private fun show() {
        mBottomSheetDialog.show()
    }

    private fun setHide() {
        KeyboardUtil.hide(mBottomSheetView)
        mBottomSheetDialog.dismiss()
        mBottomSheetBehavior.removeBottomSheetCallback(this)
    }

    private fun setLock(lock: Boolean) { mBottomSheetDialog.setCancelable(lock) }

    private fun getBottomSheetView() = mBottomSheetView

    interface Callback {
        fun onBackPressed()
        fun onClickListener(view: View)
        fun onStartTextRecognition(dialog: BottomSheetDialog)
        fun onFinishTextRecognition()
    }

    class Builder(context: AppCompatActivity, layoutID: Int, listener: Callback) {

        private var mBottomSheetHeight = context.resources.getInteger(R.integer.bottom_sheet_height)
        private var mBottomSheet = BottomSheet(context, layoutID, listener)

        fun setTitle(title: String) {
            mBottomSheet.setTitle(title)
        }

        fun setInflateMenus() {
            mBottomSheet.setInflateMenus()
        }

        fun setFavorite(note: Note) {
            mBottomSheet.setFavorite(note)
        }

        fun setOnScrollChangeListener() {
            mBottomSheet.setOnScrollChangeListener()
        }

        fun setVisibleButtons(note: Note?) {
            mBottomSheet.setVisibleButtons(note)
        }

        fun setOptions(home: Boolean) {
            mBottomSheet.setOptions(home)
        }

        fun setHide() {
            mBottomSheet.setHide()
        }

        fun setLock(lock: Boolean) {
            mBottomSheet.setLock(lock)
        }

        fun getBottomSheetHeight() = mBottomSheetHeight

        fun getBottomSheetView() = mBottomSheet.getBottomSheetView()

        fun build() {
            mBottomSheet.show()
        }
    }
}