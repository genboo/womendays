package ru.spcm.apps.womendays.view.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.view.components.CalendarGridView
import java.util.*


class MonthPageAdapter(val context: Context, private val calendar: Calendar) : PagerAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: CalendarGridView = inflater.inflate(R.layout.layout_calendar_grid, container, false) as CalendarGridView

        val days = ArrayList<Date>()
        val calendar = calendar.clone() as Calendar

        calendar.add(Calendar.MONTH, position)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val month = calendar.get(Calendar.MONTH)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val monthBeginningCell = dayOfWeek + if (dayOfWeek == calendar.firstDayOfWeek || dayOfWeek == 1) 5 else -2
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        while (days.size < 42) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val adapter = CalendarDaysAdapter(context, R.layout.list_item_day, days, month)
        view.adapter = adapter

        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return CALENDAR_SIZE
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }


    companion object {
        const val CALENDAR_SIZE = 2000
    }
}