package ru.spcm.apps.womendays.view.adapters

import android.content.Context
import android.view.ViewGroup
import ru.spcm.apps.womendays.view.components.SimplyWeekView
import java.util.*

class WeekPagerAdapter(val context: Context) : EventsPagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = SimplyWeekView(context)

        view.setWeekParams(minDate.get(Calendar.MONTH), minDate.get(Calendar.YEAR), position * WEEK_SIZE, Calendar.MONDAY)
        container.addView(view)
        return view
    }

    override fun setRange(min: Calendar, max: Calendar) {
        minDate.timeInMillis = min.timeInMillis
        maxDate.timeInMillis = max.timeInMillis
        size = ((maxDate.timeInMillis - minDate.timeInMillis) / 1000 / 60 / 60 / 24 / 7).toInt()
    }

    override fun getPositionForDay(day: Calendar): Int {
        return ((day.timeInMillis - minDate.timeInMillis) / 1000 / 60 / 60 / 24 / 7).toInt()
    }

}