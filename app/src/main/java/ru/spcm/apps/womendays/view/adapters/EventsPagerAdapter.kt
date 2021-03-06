package ru.spcm.apps.womendays.view.adapters

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import ru.spcm.apps.womendays.model.dto.EventsData
import ru.spcm.apps.womendays.tools.Logger
import java.util.*

abstract class EventsPagerAdapter : PagerAdapter() {

    val minDate: Calendar = Calendar.getInstance()
    val maxDate: Calendar = Calendar.getInstance()
    var events: EventsData = EventsData()

    var size = 0

    internal var listener: (Calendar) -> Unit = { Logger.e(this::class.java.simpleName) }

    fun setEventsList(data: EventsData) {
        events = data
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

    fun setOnDayClickListener(listener: (Calendar) -> Unit) {
        this.listener = listener
    }

    abstract fun setRange(min: Calendar, max: Calendar)

    abstract fun getPositionForDay(day: Calendar): Int

    companion object {
        const val MONTHS_IN_YEAR = 12
        const val WEEK_SIZE = 7

        const val FLAG_SEX_SAFE = 0x00000001
        const val FLAG_SEX_UNSAFE = 0x00000010
        const val FLAG_MONTHLY = 0x00000100
        const val FLAG_MONTHLY_CONFIRMED = 0x00001000
        const val FLAG_OVULATION = 0x00010000
    }

}