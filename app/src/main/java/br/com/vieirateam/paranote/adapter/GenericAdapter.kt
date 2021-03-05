package br.com.vieirateam.paranote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class GenericAdapter<T>(private val layoutID: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var genericItems = emptyList<T>()

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutID, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = genericItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindData(holder, genericItems[position])
    }

    abstract fun onBindData(holder: RecyclerView.ViewHolder, item: T)
}