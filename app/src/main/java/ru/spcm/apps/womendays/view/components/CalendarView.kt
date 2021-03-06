package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.EventsData
import ru.spcm.apps.womendays.tools.DateHelper
import ru.spcm.apps.womendays.tools.Logger
import ru.spcm.apps.womendays.view.adapters.EventsPagerAdapter
import ru.spcm.apps.womendays.view.adapters.MonthPagerAdapter
import ru.spcm.apps.womendays.view.adapters.WeekPagerAdapter
import java.util.*

class CalendarView(context: Context, attrs: AttributeSet, defStyle: Int) : FrameLayout(context, attrs, defStyle) {

    private val currentDate = GregorianCalendar()
    private var calendarPager: CalendarViewPager
    private var listener: (Calendar) -> Unit = { Logger.e(this::class.java.simpleName) }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyle, 0)
        val type = attributes.getInt(R.styleable.CalendarView_type, CALENDAR_TYPE_FULL)

        attributes.recycle()

        currentDate.firstDayOfWeek = Calendar.MONDAY

        LayoutInflater.from(context).inflate(R.layout.layout_calendar, this)
        calendarPager = findViewById(R.id.calendarViewPager)

        val minDate = DateHelper.getZeroHourCalendar()
        minDate.set(DEFAULT_START_YEAR, Calendar.JANUARY, 1)

        val maxDate = DateHelper.getZeroHourCalendar()
        maxDate.set(DEFAULT_END_YEAR, Calendar.DECEMBER, 31)

        val currentDate = DateHelper.getZeroHourCalendar()

        val adapter: EventsPagerAdapter = when (type) {
            CALENDAR_TYPE_WEEK -> WeekPagerAdapter(context)
            else -> MonthPagerAdapter(context)
        }

        adapter.setRange(minDate, maxDate)

        calendarPager.adapter = adapter
        calendarPager.currentItem = adapter.getPositionForDay(currentDate)

    }

    fun setOnDayClickListener(listener: (Calendar) -> Unit) {
        this.listener = listener
        (calendarPager.adapter as EventsPagerAdapter).setOnDayClickListener(listener)
    }

    fun setEvents(events: EventsData) {
        (calendarPager.adapter as EventsPagerAdapter).setEventsList(events)
    }

    companion object {
        const val CALENDAR_TYPE_FULL = 0
        const val CALENDAR_TYPE_WEEK = 1
        const val DEFAULT_START_YEAR = 1900
        const val DEFAULT_END_YEAR = 2100
    }


}