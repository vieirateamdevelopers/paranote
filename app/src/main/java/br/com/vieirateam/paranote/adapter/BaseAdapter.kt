package br.com.vieirateam.paranote.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(layoutID: Int) : GenericAdapter<T>(layoutID) {

    var baseItemsSelected = mutableListOf<T>()

    override fun onBindData(holder: RecyclerView.ViewHolder, item: T) {
        val view = holder.itemView

        onItemsView(item, view)

        view.setOnClickListener {
            onClick(item, view)
        }

        view.setOnLongClickListener {
            onLongClick(item, view)
            return@setOnLongClickListener true
        }
    }

    abstract fun onItemsView(item: T, view: View)

    abstract fun onClick(item: T, view: View)

    abstract fun onLongClick(item: T, view: View)

    abstract fun setItems(baseItems: List<T>)

    abstract fun getItems(): List<T>
}