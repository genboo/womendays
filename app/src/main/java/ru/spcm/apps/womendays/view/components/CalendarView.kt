package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.view.adapters.CalendarPageAdapter
import java.util.*

class CalendarView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    init {
        val inflater = LayoutInflater.from(context)

        inflater.inflate(R.layout.layout_calendar, this)

        val calendar = GregorianCalendar()
        calendar.add(Calendar.MONTH, -CalendarPageAdapter.CALENDAR_SIZE / 2)
        calendar.firstDayOfWeek = Calendar.MONDAY
        val calendarPager = findViewById<CalendarViewPager>(R.id.calendarViewPager)
        calendarPager.adapter = CalendarPageAdapter(context, calendar)
        calendarPager.currentItem = CalendarPageAdapter.CALENDAR_SIZE / 2

    }


}