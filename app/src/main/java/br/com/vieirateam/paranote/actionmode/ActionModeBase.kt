package br.com.vieirateam.paranote.actionmode

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.FragmentActivity
import br.com.vieirateam.paranote.R
import kotlinx.android.synthetic.main.adapter_card_view.view.floating_button_mini

abstract class ActionModeBase<T>(fragmentActivity: FragmentActivity?, private val archive: Boolean) : ActionMode.Callback {

    var selected = false
    private var result = false
    private var count = 0
    private var baseItems = mutableListOf<T>()
    private lateinit var actionMode: ActionMode
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
            else -> false
        }
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        actionMode = mode
        val inflater = actionMode.menuInflater
        if (archive) {
            inflater.inflate(R.menu.menu_unarchive, menu)
        } else {
            inflater.inflate(R.menu.menu_archive, menu)
        }
        actionMode.title = "1"
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        count = 0
        selected = false
        clearSelectedItems()
        baseItems.clear()
        actionMode.finish()
        mode.finish()
        if (!archive) {
            hideFloatingButton(false)
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
                count--
            } else {
                baseItems.add(item)
                addSelectedItem(item)
                setCardBackgroundColor(item, view, true)
                count++
            }
        }
        if (count == 0) actionMode.finish()
        setTitle("$count")
    }

    private fun setTitle(title: String) {
        actionMode.title = title
    }

    private fun updateItems(option: String) : Boolean {
        actionModeClicked(option)
        clearSelectedItems()
        baseItems.clear()
        actionMode.finish()
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