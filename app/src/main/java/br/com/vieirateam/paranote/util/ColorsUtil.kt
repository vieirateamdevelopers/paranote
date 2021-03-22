package br.com.vieirateam.paranote.util

import android.content.Context
import android.view.View
import androidx.core.content.res.ResourcesCompat
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.entity.Color
import br.com.vieirateam.paranote.entity.Note
import kotlinx.android.synthetic.main.adapter_card_view.view.card_view
import kotlinx.android.synthetic.main.adapter_card_view.view.text_view
import kotlinx.android.synthetic.main.bottom_sheet_color_dark.view.*
import kotlinx.android.synthetic.main.bottom_sheet_color_light.view.*
import kotlinx.android.synthetic.main.bottom_sheet_text.view.*
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.*

object ColorsUtil {

    fun getColors(view: View?): MutableList<Color> {
        val listColors = mutableListOf<Color>()
        if (UserPreferenceUtil.darkMode) {
            listColors.add(Color(1, R.color.colorDarkToolbar, view?.image_view_color_dark_1))
            listColors.add(Color(2, R.color.colorDarkMode2, view?.image_view_color_dark_2))
            listColors.add(Color(3, R.color.colorDarkMode3, view?.image_view_color_dark_3))
            listColors.add(Color(4, R.color.colorDarkMode4, view?.image_view_color_dark_4))
            listColors.add(Color(5, R.color.colorDarkMode5, view?.image_view_color_dark_5))
            listColors.add(Color(6, R.color.colorDarkMode6, view?.image_view_color_dark_6))
            listColors.add(Color(7, R.color.colorDarkMode7, view?.image_view_color_dark_7))
            listColors.add(Color(8, R.color.colorDarkMode8, view?.image_view_color_dark_8))
            listColors.add(Color(9, R.color.colorDarkMode9, view?.image_view_color_dark_9))
            listColors.add(Color(10, R.color.colorDarkMode10, view?.image_view_color_dark_10))
            listColors.add(Color(11, R.color.colorDarkMode11, view?.image_view_color_dark_11))
            listColors.add(Color(12, R.color.colorDarkMode12, view?.image_view_color_dark_12))
            listColors.add(Color(13, R.color.colorDarkMode13, view?.image_view_color_dark_13))
            listColors.add(Color(14, R.color.colorDarkMode14, view?.image_view_color_dark_14))
            listColors.add(Color(15, R.color.colorDarkMode15, view?.image_view_color_dark_15))
            listColors.add(Color(16, R.color.colorDarkMode16, view?.image_view_color_dark_16))
            listColors.add(Color(17, R.color.colorDarkMode17, view?.image_view_color_dark_17))
            listColors.add(Color(18, R.color.colorDarkMode18, view?.image_view_color_dark_18))
            listColors.add(Color(19, R.color.colorDarkMode19, view?.image_view_color_dark_19))
            listColors.add(Color(20, R.color.colorDarkMode20, view?.image_view_color_dark_20))
        } else {
            listColors.add(Color(1, R.color.colorLightMode, view?.image_view_color_light_1))
            listColors.add(Color(2, R.color.colorLightMode2, view?.image_view_color_light_2))
            listColors.add(Color(3, R.color.colorLightMode3, view?.image_view_color_light_3))
            listColors.add(Color(4, R.color.colorLightMode4, view?.image_view_color_light_4))
            listColors.add(Color(5, R.color.colorLightMode5, view?.image_view_color_light_5))
            listColors.add(Color(6, R.color.colorLightMode6, view?.image_view_color_light_6))
            listColors.add(Color(7, R.color.colorLightMode7, view?.image_view_color_light_7))
            listColors.add(Color(8, R.color.colorLightMode8, view?.image_view_color_light_8))
            listColors.add(Color(9, R.color.colorLightMode9, view?.image_view_color_light_9))
            listColors.add(Color(10, R.color.colorLightMode10, view?.image_view_color_light_10))
            listColors.add(Color(11, R.color.colorLightMode11, view?.image_view_color_light_11))
            listColors.add(Color(12, R.color.colorLightMode12, view?.image_view_color_light_12))
            listColors.add(Color(13, R.color.colorLightMode13, view?.image_view_color_light_13))
            listColors.add(Color(14, R.color.colorLightMode14, view?.image_view_color_light_14))
            listColors.add(Color(15, R.color.colorLightMode15, view?.image_view_color_light_15))
            listColors.add(Color(16, R.color.colorLightMode16, view?.image_view_color_light_16))
            listColors.add(Color(17, R.color.colorLightMode17, view?.image_view_color_light_17))
            listColors.add(Color(18, R.color.colorLightMode18, view?.image_view_color_light_18))
            listColors.add(Color(19, R.color.colorLightMode19, view?.image_view_color_light_19))
            listColors.add(Color(20, R.color.colorLightMode20, view?.image_view_color_light_20))
        }
        return listColors
    }

    fun setColor(note: Note, color: Color) {
        val context = color.circleImageView?.context
        if (context != null) {
            if (note.color == color.id) {
                color.circleImageView.circleBackgroundColor = ResourcesCompat.getColor(context.resources, color.colorResource, null)
                color.circleImageView.setImageResource(R.drawable.ic_drawable_check_color)
            }
        }
    }
    
    fun setBackgroundColor(item: Note, view: View) {
        val context = view.context
        val colors = getColors(view)
        for (color in colors) {
            if (item.color == color.id) {
                val backgroundColor = ResourcesCompat.getColor(context.resources, color.colorResource, null)
                view.bottom_sheet_text.setBackgroundColor(backgroundColor)
                view.material_bottom_toolbar.setBackgroundColor(backgroundColor)
                break
            }
        }
    }

    fun setCardBackgroundColor(item: Note, view: View, selected: Boolean) {
        val context = view.context
        var backgroundColor = 0
        val backgroundViewColor: Int
        if (selected) {
            backgroundColor = ResourcesCompat.getColor(context.resources, R.color.colorLightHint, null)
            backgroundViewColor = ResourcesCompat.getColor(context.resources, R.color.colorLightMode, null)
        } else {
            val colors = getColors(null)
            for (color in colors) {
                if (item.color == color.id) {
                    backgroundColor = ResourcesCompat.getColor(context.resources, color.colorResource, null)
                    break
                }
            }
            backgroundViewColor = getBackgroundViewsColor(context)
        }
        view.card_view.setCardBackgroundColor(backgroundColor)
        view.text_view.setTextColor(backgroundViewColor)
    }

    private fun getBackgroundViewsColor(context: Context): Int {
        return if (UserPreferenceUtil.darkMode) {
            ResourcesCompat.getColor(context.resources, R.color.colorDarkViews, null)
        } else {
            ResourcesCompat.getColor(context.resources, R.color.colorLightViews, null)
        }
    }
}