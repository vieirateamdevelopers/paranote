package br.com.vieirateam.paranote.util

import android.view.View
import androidx.core.content.res.ResourcesCompat
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Note
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.adapter_card_view.view.card_view
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view
import kotlinx.android.synthetic.main.bottom_sheet_base.view.bottom_sheet_base
import kotlinx.android.synthetic.main.bottom_sheet_base.view.text_input_base_title
import kotlinx.android.synthetic.main.bottom_sheet_base.view.text_input_base_body
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_1
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_2
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_3
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_4
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_5
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_6
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_7
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_8
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_9
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_10
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_11
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_12
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_13
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_14
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_15
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_16
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_17
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_18
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_19
import kotlinx.android.synthetic.main.bottom_sheet_color.view.image_view_color_20
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.material_bottom_toolbar

object ColorsUtil {

    fun getColors(view: View): MutableList<Pair<Int?, CircleImageView>> {
        val listColors = mutableListOf<Pair<Int?, CircleImageView>>()
        listColors.add(Pair(null, view.image_view_color_1))
        listColors.add(Pair(R.color.colorSheet2, view.image_view_color_2))
        listColors.add(Pair(R.color.colorSheet3, view.image_view_color_3))
        listColors.add(Pair(R.color.colorSheet4, view.image_view_color_4))
        listColors.add(Pair(R.color.colorSheet5, view.image_view_color_5))
        listColors.add(Pair(R.color.colorSheet6, view.image_view_color_6))
        listColors.add(Pair(R.color.colorSheet7, view.image_view_color_7))
        listColors.add(Pair(R.color.colorSheet8, view.image_view_color_8))
        listColors.add(Pair(R.color.colorSheet9, view.image_view_color_9))
        listColors.add(Pair(R.color.colorSheet10, view.image_view_color_10))
        listColors.add(Pair(R.color.colorSheet11, view.image_view_color_11))
        listColors.add(Pair(R.color.colorSheet12, view.image_view_color_12))
        listColors.add(Pair(R.color.colorSheet13, view.image_view_color_13))
        listColors.add(Pair(R.color.colorSheet14, view.image_view_color_14))
        listColors.add(Pair(R.color.colorSheet15, view.image_view_color_15))
        listColors.add(Pair(R.color.colorSheet16, view.image_view_color_16))
        listColors.add(Pair(R.color.colorSheet17, view.image_view_color_17))
        listColors.add(Pair(R.color.colorSheet18, view.image_view_color_18))
        listColors.add(Pair(R.color.colorSheet19, view.image_view_color_19))
        listColors.add(Pair(R.color.colorSheet20, view.image_view_color_20))
        return listColors
    }

    fun setCircleBackgroundColor(item: Note, color: Int?, view: CircleImageView) {
        if (item.color == color) {
            val context = view.context
            if (color == null) {
                if (UserPreferenceUtil.darkMode) {
                    view.circleBackgroundColor = ResourcesCompat.getColor(context.resources, R.color.colorDarkToolbar, null)
                    view.setImageResource(R.drawable.ic_drawable_check_color_white)
                } else {
                    view.circleBackgroundColor = ResourcesCompat.getColor(context.resources, R.color.colorLightToolbar, null)
                    view.setImageResource(R.drawable.ic_drawable_check_color_black)
                }
            } else {
                view.circleBackgroundColor = ResourcesCompat.getColor(context.resources, color, null)
                if (checkBackgroundTextColor(color)) {
                    view.setImageResource(R.drawable.ic_drawable_check_color_white)
                } else {
                    view.setImageResource(R.drawable.ic_drawable_check_color_black)
                }
            }
        }
    }
    
    fun setBackgroundColor(item: Note, view: View) {
        val context = view.context
        val backgroundColor: Int
        val backgroundToolbarColor: Int
        var backgroundTextColor: Int = ResourcesCompat.getColor(context.resources, R.color.colorDarkMode, null)
        if (item.color == null) {
            if (UserPreferenceUtil.darkMode) {
                backgroundColor = ResourcesCompat.getColor(context.resources, R.color.colorDarkMode, null)
                backgroundTextColor = ResourcesCompat.getColor(context.resources, R.color.colorLightMode, null)
                backgroundToolbarColor = ResourcesCompat.getColor(context.resources, R.color.colorDarkToolbar, null)
            } else {
                backgroundColor = ResourcesCompat.getColor(context.resources, R.color.colorLightMode, null)
                backgroundTextColor = ResourcesCompat.getColor(context.resources, R.color.colorLightViews, null)
                backgroundToolbarColor = ResourcesCompat.getColor(context.resources, R.color.colorAccent, null)
            }
        } else {
            val color = item.color as Int
            backgroundColor = ResourcesCompat.getColor(context.resources, color, null)
            backgroundToolbarColor = ResourcesCompat.getColor(context.resources, color, null)
            if (checkBackgroundTextColor(color)) {
                backgroundTextColor = ResourcesCompat.getColor(context.resources, R.color.colorDarkViews, null)
            }
        }
        view.material_bottom_toolbar.setBackgroundColor(backgroundToolbarColor)
        view.bottom_sheet_base.setBackgroundColor(backgroundColor)
        view.text_input_base_title.setTextColor(backgroundTextColor)
        view.text_input_base_title.setHintTextColor(backgroundTextColor)
        view.text_input_base_body.setTextColor(backgroundTextColor)
        view.text_input_base_body.setHintTextColor(backgroundTextColor)
    }

    fun setCardBackgroundColor(item: Note, view: View, selected: Boolean) {
        val context = view.context
        val backgroundColor: Int
        var backgroundTextColor = ResourcesCompat.getColor(context.resources, R.color.colorDarkMode, null)
        if (selected) {
            backgroundColor = ResourcesCompat.getColor(context.resources, R.color.colorTextHint, null)
            backgroundTextColor = ResourcesCompat.getColor(context.resources, R.color.colorDarkMode, null)
        } else {
            if (item.color == null) {
                if (UserPreferenceUtil.darkMode) {
                    backgroundColor = ResourcesCompat.getColor(context.resources, R.color.colorDarkToolbar, null)
                    backgroundTextColor = ResourcesCompat.getColor(context.resources, R.color.colorLightMode, null)
                } else {
                    backgroundColor = ResourcesCompat.getColor(context.resources, R.color.colorLightMode, null)
                    backgroundTextColor = ResourcesCompat.getColor(context.resources, R.color.colorLightViews, null)
                }
            } else {
                val color = item.color as Int
                backgroundColor = ResourcesCompat.getColor(context.resources, color, null)
                if (checkBackgroundTextColor(color)) {
                    backgroundTextColor = ResourcesCompat.getColor(context.resources, R.color.colorLightMode, null)
                }
            }
        }
        view.card_view.setCardBackgroundColor(backgroundColor)
        view.text_view.setTextColor(backgroundTextColor)
    }

    private fun checkBackgroundTextColor(color: Int): Boolean {
        return when(color) {
            R.color.colorSheet4 -> true
            R.color.colorSheet5 -> true
            R.color.colorSheet6 -> true
            R.color.colorSheet18 -> true
            else -> false
        }
    }
}