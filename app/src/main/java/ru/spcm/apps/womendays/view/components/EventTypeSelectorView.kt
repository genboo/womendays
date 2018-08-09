package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.Event
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable


class EventTypeSelectorView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var currentSelected: ImageView? = null

    init {
        orientation = LinearLayout.HORIZONTAL
        Event.Type.values().forEach {
            if (it != Event.Type.OVULATION && it != Event.Type.MONTHLY) {
                val image = ImageView(context, attrs, 0, R.style.Button_Selector)
                if (currentSelected == null) {
                    currentSelected = image
                    image.isSelected = true
                }
                image.isClickable = true
                image.isFocusable = true

                val icon = getDrawable(it)
                icon?.colorFilter = PorterDuffColorFilter(getColor(it), PorterDuff.Mode.SRC_IN)
                image.setImageDrawable(icon)

                image.setOnClickListener { v ->
                    if (v != currentSelected) {
                        v.isSelected = true
                        currentSelected?.isSelected = false
                        currentSelected = v as ImageView
                        v.tag = it
                    }
                }

                addView(image)
            }
        }
    }

    fun getSelectedType(): Event.Type {
        return currentSelected?.tag as Event.Type
    }

    private fun getDrawable(type: Event.Type): Drawable? {
        return when (type) {
            Event.Type.SEX_SAFE -> ContextCompat.getDrawable(context, R.drawable.ic_heart)
            Event.Type.SEX_UNSAFE -> ContextCompat.getDrawable(context, R.drawable.ic_heart_broken)
            Event.Type.MONTHLY, Event.Type.MONTHLY_CONFIRMED -> ContextCompat.getDrawable(context, R.drawable.ic_water)
            Event.Type.MESSAGE -> ContextCompat.getDrawable(context, R.drawable.ic_message)
            else -> ContextCompat.getDrawable(context, R.drawable.ic_close_circle)
        }
    }

    private fun getColor(type: Event.Type): Int {
        return when (type) {
            Event.Type.SEX_SAFE, Event.Type.SEX_UNSAFE -> ContextCompat.getColor(context, R.color.colorAccent)
            Event.Type.MONTHLY, Event.Type.MONTHLY_CONFIRMED -> ContextCompat.getColor(context, R.color.colorMonthlyConfirmed)
            else -> ContextCompat.getColor(context, R.color.colorPrimaryDark)
        }
    }

}