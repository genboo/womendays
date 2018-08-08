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
            Event.Type.OVULATION -> resources.getString(R.string.event_type_ovulation)
            Event.Type.MESSAGE -> resources.getString(R.string.event_type_message)
        }
        if (item.message.isEmpty()) {
            message.text = ""
            message.visibility = View.GONE
        } else {
            message.text = item.message
            message.visibility = View.VISIBLE
        }

        if (item.type == Event.Type.MONTHLY) {
            delete.visibility = View.GONE
        } else {
            delete.visibility = View.VISIBLE
        }
    }

    fun setListener(listener: View.OnClickListener) {
        itemView.delete.setOnClickListener(listener)
    }

}