package ru.spcm.apps.womendays.view.adapters

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.tools.DateHelper
import java.util.*
import kotlin.collections.HashMap

abstract class EventsPagerAdapter() : PagerAdapter() {

    val minDate: Calendar = Calendar.getInstance()
    val maxDate: Calendar = Calendar.getInstance()
    val currentDate: Calendar = Calendar.getInstance()

    var size = 0

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

    override fun getCount(): Int {
        return size
    }

    fun setDate(date: Calendar) {
        currentDate.timeInMillis = date.timeInMillis
    }

    fun getMonthForPosition(position: Int): Int {
        return (position + minDate.get(Calendar.MONTH)) % MONTHS_IN_YEAR
    }

    fun getYearForPosition(position: Int): Int {
        val yearOffset = (position + minDate.get(Calendar.MONTH)) / MONTHS_IN_YEAR
        return yearOffset + minDate.get(Calendar.YEAR)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    abstract fun setRange(min: Calendar, max: Calendar)

    abstract fun getPositionForDay(day: Calendar): Int

    companion object {
        const val MONTHS_IN_YEAR = 12
        const val WEEK_SIZE = 7
    }

}