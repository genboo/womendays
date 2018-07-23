package ru.spcm.apps.womendays.view.adapters

import android.content.Context
import android.view.ViewGroup
import java.util.*


class MonthPageAdapter(context: Context, private val calendar: Calendar) : EventsPagerAdapter(context) {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val calendar = calendar.clone() as Calendar

        calendar.add(Calendar.MONTH, position)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        return createView(container, calendar, calendar.get(Calendar.MONTH), 42)
    }


}