package ru.spcm.apps.womendays.view.adapters

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.tools.DateHelper
import java.util.*
import kotlin.collections.HashMap

abstract class EventsPagerAdapter : PagerAdapter() {

    val minDate: Calendar = Calendar.getInstance()
    val maxDate: Calendar = Calendar.getInstance()
    val events: HashMap<String, Int> = HashMap()

    var size = 0

    fun setEvents(event: List<Event>) {
        events.clear()
        event.forEach {
            val date = DateHelper.formatYearMonthDay(it.date)
            var flag: Int = events[date] ?: 0

            flag = when (it.type) {
                Event.Type.SEX_SAFE -> flag or FLAG_SEX_SAFE
                Event.Type.SEX_UNSAFE -> flag or FLAG_SEX_UNSAFE
                Event.Type.MONTHLY -> flag or FLAG_SEX_MONTHLY
            }
            events[date] = flag
        }
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return size
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

        const val FLAG_SEX_SAFE = 0x00000001
        const val FLAG_SEX_UNSAFE = 0x00000010
        const val FLAG_SEX_MONTHLY = 0x00000100
    }

}