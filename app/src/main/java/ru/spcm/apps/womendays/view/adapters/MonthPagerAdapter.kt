package ru.spcm.apps.womendays.view.adapters

import android.content.Context
import android.view.ViewGroup
import ru.spcm.apps.womendays.view.components.SimplyMonthView
import java.util.*

class MonthPagerAdapter(val context: Context) : EventsPagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = SimplyMonthView(context)
        view.setMonthParams(getMonthForPosition(position), getYearForPosition(position), Calendar.MONDAY)
        container.addView(view)
        return view
    }

    override fun setRange(min: Calendar, max: Calendar) {
        minDate.timeInMillis = min.timeInMillis
        maxDate.timeInMillis = max.timeInMillis

        val diffYear = maxDate.get(Calendar.YEAR) - minDate.get(Calendar.YEAR)
        val diffMonth = maxDate.get(Calendar.MONTH) - minDate.get(Calendar.MONTH)

        size = diffMonth + MONTHS_IN_YEAR * diffYear + 1
    }

    override fun getPositionForDay(day: Calendar): Int {
        val yearOffset = day.get(Calendar.YEAR) - minDate.get(Calendar.YEAR)
        val monthOffset = day.get(Calendar.MONTH) - minDate.get(Calendar.MONTH)
        return yearOffset * MONTHS_IN_YEAR + monthOffset
    }

}