package ru.spcm.apps.womendays.view.adapters

import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.View
import java.util.ArrayList

abstract class RecyclerViewAdapter<T, H : RecyclerView.ViewHolder> internal constructor(items: List<T>?) : RecyclerView.Adapter<H>() {

    private var items: List<T>

    private var listener: (Int, T, View?) -> Unit = {_, _, _ -> }

    private val loadedPage = SparseBooleanArray()

    init {
        if (items == null) {
            this.items = ArrayList()
        } else {
            this.items = items
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getSize(): Int {
        return items.size
    }

    open fun setItems(items: List<T>) {
        this.items = items
    }

    fun getItem(position: Int): T {
        return items[position]
    }

    fun getItems(): List<T> {
        return items
    }

    fun setOnItemClickListener(event: (Int, T, View?) -> Unit) {
        listener = event
    }

    internal fun onItemClick(view: View, position: Int, imageView: View? = null) {
        if (position != RecyclerView.NO_POSITION) {
            view.postDelayed({ listener(position, items[position], imageView) }, DEFAULT_CLICK_DELAY)
        }
    }

    companion object {
        private const val DEFAULT_CLICK_DELAY: Long = 100
    }

}
