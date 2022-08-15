package br.com.vieirateam.paranote.actionmode

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.FragmentActivity
import br.com.vieirateam.paranote.R
import kotlinx.android.synthetic.main.adapter_card_view.view.*

abstract class ActionModeBase<T>(fragmentActivity: FragmentActivity?, private val fragmentTAG: String) : ActionMode.Callback {

    var selected = false
    private var result = false
    private var count = 0
    private var baseItems = mutableListOf<T>()
    private lateinit var mActionMode: ActionMode
    protected var context = fragmentActivity as FragmentActivity

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_copy -> return updateItems("copy")
            R.id.menu_share -> return updateItems("share")
            R.id.menu_archive -> return updateItems("archive")
            R.id.menu_unarchive -> return updateItems("archive")
            R.id.menu_delete -> return updateItems("delete")
            R.id.menu_duplicate -> return updateItems("duplicate")
            R.id.menu_favorite -> return updateItems("favorite")
            R.id.menu_color -> return updateItems("color")
            else -> false
        }
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mActionMode = mode
        val inflater = mActionMode.menuInflater
        when(fragmentTAG) {
            "home" -> inflater.inflate(R.menu.menu_archive, menu)
            "favorite" -> inflater.inflate(R.menu.menu_archive, menu)
            "reminder" -> inflater.inflate(R.menu.menu_archive, menu)
            "archive" -> inflater.inflate(R.menu.menu_unarchive, menu)
        }
        mActionMode.title = "1"
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        count = 0
        selected = false
        clearSelectedItems()
        baseItems.clear()
        mActionMode.finish()
        mode.finish()
        when(fragmentTAG) {
            "home" -> hideFloatingButton(false)
            "favorite" -> hideFloatingButton(false)
            "reminder" -> hideFloatingButton(true)
            "archive" -> hideFloatingButton(true)
        }
        notifyDataSetChanged()
    }

    fun selectedItem(item: T, view: View) {
        view.floating_button_mini.visibility = View.INVISIBLE
        if (selected) {
            if (baseItems.contains(item)) {
                baseItems.remove(item)
                removeSelectedItem(item)
                setCardBackgroundColor(item, view, false)
                view.floating_button_check.hide()
                count--
            } else {
                baseItems.add(item)
                addSelectedItem(item)
                setCardBackgroundColor(item, view, true)
                view.floating_button_check.show()
                count++
            }
        }
        if (count == 0) mActionMode.finish()
        setTitle("$count")
    }

    private fun setTitle(title: String) {
        mActionMode.title = title
    }

    private fun updateItems(option: String) : Boolean {
        actionModeClicked(option)
        clearSelectedItems()
        baseItems.clear()
        mActionMode.finish()
        return true
    }

    abstract fun addSelectedItem(item: T)

    abstract fun removeSelectedItem(item: T)

    abstract fun clearSelectedItems()

    abstract fun actionModeClicked(option: String)

    abstract fun notifyDataSetChanged()

    abstract fun setCardBackgroundColor(item: T, view: View, selected: Boolean)

    abstract fun hideFloatingButton(hide: Boolean)
}