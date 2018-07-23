package ru.spcm.apps.womendays.view.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.tools.DateHelper
import ru.spcm.apps.womendays.view.components.CalendarGridView
import java.util.*
import kotlin.collections.HashMap

open class EventsPagerAdapter(val context: Context) : PagerAdapter() {

    private val inflater = LayoutInflater.from(context)

    private val events: HashMap<String, ArrayList<Event>> = HashMap()

    fun setEvents(event: List<Event>) {
        event.forEach {
            val date = DateHelper.formatYearMonthDay(it.date)
            if (events.containsKey(date)) {
                (events[date] as ArrayList<Event>).add(it)
            } else {
                val list = ArrayList<Event>()
                list.add(it)
                events[date] = list
            }
        }
        notifyDataSetChanged()
    }

    internal fun createView(container: ViewGroup, calendar: Calendar, month: Int?, size: Int): View {
        val view: CalendarGridView = inflater.inflate(R.layout.layout_calendar_grid, container, false) as CalendarGridView

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val monthBeginningCell = dayOfWeek + if ((dayOfWeek == calendar.firstDayOfWeek && month != null) || dayOfWeek == 1) 5 else -2
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        val days = ArrayList<Date>()
        while (days.size < size) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val adapter = CalendarDaysAdapter(context, R.layout.list_item_day, days, events, month)
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

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    companion object {
        const val CALENDAR_SIZE = 2000
    }
}