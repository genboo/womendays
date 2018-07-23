package ru.spcm.apps.womendays.view.adapters

import android.content.Context
import android.view.ViewGroup
import java.util.*


class WeekPageAdapter(context: Context, private val calendar: Calendar) : EventsPagerAdapter(context) {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val calendar = calendar.clone() as Calendar
        calendar.add(Calendar.WEEK_OF_YEAR, position)
        return createView(container, calendar, null, 7)
    }

}