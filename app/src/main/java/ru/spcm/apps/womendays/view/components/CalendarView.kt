package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.view.adapters.EventsPagerAdapter
import ru.spcm.apps.womendays.view.adapters.MonthPageAdapter
import ru.spcm.apps.womendays.view.adapters.WeekPageAdapter
import java.util.*

class CalendarView(context: Context, attrs: AttributeSet, defStyle: Int) : FrameLayout(context, attrs, defStyle) {

    private val currentDate = GregorianCalendar()
    private lateinit var calendarPager: CalendarViewPager

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyle, 0)
        val type = attributes.getInt(R.styleable.CalendarView_type, CALENDAR_TYPE_FULL)

        attributes.recycle()

        currentDate.firstDayOfWeek = Calendar.MONDAY

        when (type) {
            CALENDAR_TYPE_WEEK -> prepareWeekCalendar()
            else -> prepareFullCalendar()
        }

    }

    fun setEvents(events: List<Event>) {
        (calendarPager.adapter as EventsPagerAdapter).setEvents(events)
    }

    private fun prepareWeekCalendar() {
        LayoutInflater.from(context).inflate(R.layout.layout_calendar_week, this)
        calendarPager = findViewById(R.id.calendarViewPager)

        currentDate.add(Calendar.WEEK_OF_YEAR, -EventsPagerAdapter.CALENDAR_SIZE / 2)

        calendarPager.adapter = WeekPageAdapter(context, currentDate)
        calendarPager.currentItem = EventsPagerAdapter.CALENDAR_SIZE / 2
    }

    private fun prepareFullCalendar() {
        LayoutInflater.from(context).inflate(R.layout.layout_calendar, this)
        calendarPager = findViewById(R.id.calendarViewPager)

        currentDate.add(Calendar.MONTH, -EventsPagerAdapter.CALENDAR_SIZE / 2)

        val currentDateLabel = findViewById<TextView>(R.id.currentDateLabel)

        if (!isInEditMode) {
            calendarPager.clearOnPageChangeListeners()
            calendarPager.addOnPageChangeListener(CalendarOnPageChangeListener(currentDateLabel, currentDate))
        }
        currentDateLabel.setOnClickListener { calendarPager.currentItem = EventsPagerAdapter.CALENDAR_SIZE / 2 }

        calendarPager.adapter = MonthPageAdapter(context, currentDate)
        calendarPager.currentItem = EventsPagerAdapter.CALENDAR_SIZE / 2
    }

    companion object {
        const val CALENDAR_TYPE_FULL = 0
        const val CALENDAR_TYPE_WEEK = 1
    }

}