package ru.spcm.apps.womendays.view.adapters.other

import android.support.v7.util.DiffUtil
import ru.spcm.apps.womendays.model.dto.Event

class EventsDiffCallback(private val oldList: List<Event>,
                         private val newList: List<Event>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

}