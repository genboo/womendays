package ru.spcm.apps.womendays.view.adapters

import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.view.adapters.holders.EventHolder
import ru.spcm.apps.womendays.view.adapters.other.EventsDiffCallback

/**
 * Адаптер для событий
 * Created by gen on 06.08.2018.
 */

class EventsListAdapter(items: List<Event>?) : RecyclerViewAdapter<Event, EventHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_event, parent, false)
        val holder = EventHolder(view)
        holder.setListener(View.OnClickListener { v -> onItemClick(v, holder.adapterPosition) })
        return holder
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        holder.bind(getItem(holder.adapterPosition))
    }

    override fun setItems(items: List<Event>) {
        val diffs = DiffUtil.calculateDiff(EventsDiffCallback(getItems(), items), !getItems().isEmpty())
        super.setItems(items)
        diffs.dispatchUpdatesTo(this)
    }

}
