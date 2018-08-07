package ru.spcm.apps.womendays.view.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_event.view.*
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.Event

class EventHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Event) = with(itemView) {
        label.text = when (item.type) {
            Event.Type.MONTHLY_CONFIRMED -> resources.getString(R.string.event_type_monthly)
            Event.Type.MONTHLY -> resources.getString(R.string.event_type_monthly)
            Event.Type.SEX_SAFE -> resources.getString(R.string.event_type_sex)
            Event.Type.SEX_UNSAFE -> resources.getString(R.string.event_type_sex_unsafe)
        }
    }

    fun setListener(listener: View.OnClickListener) {
        itemView.itemBlock.setOnClickListener(listener)
    }

}