package ru.spcm.apps.womendays.view.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_event.view.*
import ru.spcm.apps.womendays.model.dto.Event

class EventHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Event) = with(itemView) {
        label.text = item.type.name
    }

    fun setListener(listener: View.OnClickListener) {
        itemView.itemBlock.setOnClickListener(listener)
    }

}