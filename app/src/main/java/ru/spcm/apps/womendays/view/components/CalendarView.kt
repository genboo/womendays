package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.view.adapters.CalendarPageAdapter
import java.util.*

class CalendarView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val currentDate = GregorianCalendar()
    private var calendarPager: CalendarViewPager

    init {
        val inflater = LayoutInflater.from(context)

        inflater.inflate(R.layout.layout_calendar, this)

        currentDate.add(Calendar.MONTH, -CalendarPageAdapter.CALENDAR_SIZE / 2)
        currentDate.firstDayOfWeek = Calendar.MONDAY

        calendarPager = findViewById(R.id.calendarViewPager)
        val currentDateLabel = findViewById<TextView>(R.id.currentDateLabel)

        if (!isInEditMode) {
            calendarPager.clearOnPageChangeListeners()
            calendarPager.addOnPageChangeListener(CalendarOnPageChangeListener(currentDateLabel, currentDate))
        }
        currentDateLabel.setOnClickListener { calendarPager.currentItem = CalendarPageAdapter.CALENDAR_SIZE / 2 }

        calendarPager.adapter = CalendarPageAdapter(context, currentDate)
        calendarPager.currentItem = CalendarPageAdapter.CALENDAR_SIZE / 2

    }

}