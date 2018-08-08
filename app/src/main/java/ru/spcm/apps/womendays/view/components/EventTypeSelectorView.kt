package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.Event

class EventTypeSelectorView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        orientation = LinearLayout.HORIZONTAL
        Event.Type.values().forEach {
            val image = ImageView(context, attrs)
            image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart))
            addView(image)
        }
    }

}