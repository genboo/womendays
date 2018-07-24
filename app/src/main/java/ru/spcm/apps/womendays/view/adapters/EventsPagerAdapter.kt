package ru.spcm.apps.womendays.view.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.tools.DateHelper
import ru.spcm.apps.womendays.view.components.SimplyMonthView
import java.util.*
import kotlin.collections.HashMap

class EventsPagerAdapter(val context: Context) : PagerAdapter() {

    private val minDate = Calendar.getInstance()
    private val maxDate = Calendar.getInstance()
    private val currentDate = Calendar.getInstance()

    private var count = 0

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

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = SimplyMonthView(context)
        view.setMonthParams(getMonthForPosition(position), getYearForPosition(position), Calendar.MONDAY)
        container.addView(view)
        return view
    }

    fun setRange(min: Calendar, max: Calendar) {
        minDate.timeInMillis = min.timeInMillis
        maxDate.timeInMillis = max.timeInMillis

        val diffYear = maxDate.get(Calendar.YEAR) - minDate.get(Calendar.YEAR)
        val diffMonth = maxDate.get(Calendar.MONTH) - minDate.get(Calendar.MONTH)

        count = diffMonth + MONTHS_IN_YEAR * diffYear + 1

    }

    fun setDate(date: Calendar) {
        currentDate.timeInMillis = date.timeInMillis
    }

    fun getPositionForDay(day: Calendar): Int {
        val yearOffset = day.get(Calendar.YEAR) - minDate.get(Calendar.YEAR)
        val monthOffset = day.get(Calendar.MONTH) - minDate.get(Calendar.MONTH)
        return yearOffset * MONTHS_IN_YEAR + monthOffset
    }

    private fun getMonthForPosition(position: Int): Int {
        return (position + minDate.get(Calendar.MONTH)) % MONTHS_IN_YEAR
    }

    private fun getYearForPosition(position: Int): Int {
        val yearOffset = (position + minDate.get(Calendar.MONTH)) / MONTHS_IN_YEAR
        return yearOffset + minDate.get(Calendar.YEAR)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return count
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    companion object {
        const val MONTHS_IN_YEAR = 12
    }

}