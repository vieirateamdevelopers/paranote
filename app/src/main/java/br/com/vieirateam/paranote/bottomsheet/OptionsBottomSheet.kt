package br.com.vieirateam.paranote.bottomsheet

import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Note
import kotlinx.android.synthetic.main.bottom_sheet_options.view.floating_button_option_1
import kotlinx.android.synthetic.main.bottom_sheet_options.view.floating_button_option_2
import kotlinx.android.synthetic.main.bottom_sheet_options.view.floating_button_option_3
import kotlinx.android.synthetic.main.bottom_sheet_options.view.floating_button_option_4
import kotlinx.android.synthetic.main.bottom_sheet_options.view.floating_button_option_5
import kotlinx.android.synthetic.main.bottom_sheet_options.view.text_view_option_1
import kotlinx.android.synthetic.main.bottom_sheet_options.view.text_view_option_2
import kotlinx.android.synthetic.main.bottom_sheet_options.view.text_view_option_3
import kotlinx.android.synthetic.main.bottom_sheet_options.view.text_view_option_4
import kotlinx.android.synthetic.main.bottom_sheet_options.view.text_view_option_5

class OptionsBottomSheet(
    private val context: AppCompatActivity,
    listener: Callback,
    private val note: Note?,
    private val home: Boolean): BaseBottomSheet(context, listener, R.layout.bottom_sheet_options) {

    override fun build() {
        configureBottomSheetOptions(home)
        mView.floating_button_option_1.setOnClickListener {
            listener.setOnBottomSheetClickListener("option1")
            hide()
        }

        mView.floating_button_option_2.setOnClickListener {
            listener.setOnBottomSheetClickListener("option2")
            hide()
        }

        mView.floating_button_option_3.setOnClickListener {
            listener.setOnBottomSheetClickListener("option3")
            hide()
        }

        mView.floating_button_option_4.setOnClickListener {
            listener.setOnBottomSheetClickListener("option4")
            hide()
        }

        mView.floating_button_option_5.setOnClickListener {
            listener.setOnBottomSheetClickListener("option5")
            hide()
        }
    }

    private fun configureBottomSheetOptions(home: Boolean) {
        if (home) {
            mView.floating_button_option_1.setImageResource(R.drawable.ic_drawable_camera)
            mView.floating_button_option_2.setImageResource(R.drawable.ic_drawable_draw)
            mView.floating_button_option_3.setImageResource(R.drawable.ic_drawable_image)
            mView.floating_button_option_4.setImageResource(R.drawable.ic_drawable_voice)
            mView.floating_button_option_5.setImageResource(R.drawable.ic_drawable_keyboard)

            mView.text_view_option_1.text = context.getString(R.string.menu_camera)
            mView.text_view_option_2.text = context.getString(R.string.menu_draw)
            mView.text_view_option_3.text = context.getString(R.string.menu_upload)
            mView.text_view_option_4.text = context.getString(R.string.menu_voice)
            mView.text_view_option_5.text = context.getString(R.string.menu_keyboard)
        } else {
            if (note != null) setFavoriteNote(note.favorite)
            mView.floating_button_option_1.setImageResource(R.drawable.ic_drawable_share)
            mView.floating_button_option_2.setImageResource(R.drawable.ic_drawable_copy)
            mView.floating_button_option_3.setImageResource(R.drawable.ic_drawable_color)
            mView.floating_button_option_5.setImageResource(R.drawable.ic_drawable_notification)

            mView.text_view_option_1.text = context.getString(R.string.menu_share)
            mView.text_view_option_2.text = context.getString(R.string.menu_copy)
            mView.text_view_option_3.text = context.getString(R.string.menu_color)
            mView.text_view_option_4.text = context.getString(R.string.menu_favorite)
            mView.text_view_option_5.text = context.getString(R.string.menu_reminder)
        }
    }
}