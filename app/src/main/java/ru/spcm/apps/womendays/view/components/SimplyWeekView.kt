package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.graphics.Canvas
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.tools.DateHelper
import java.util.*


class SimplyWeekView(context: Context) : CalendarPageView(context) {

    init {
        val padding = context.resources.getDimensionPixelSize(R.dimen.calendar_month_padding)
        setPadding(padding, padding, padding, padding)

        monthLabelHeight = 0f
        weekInMonth = 1
    }

    override fun onDraw(canvas: Canvas) {
        drawDaysOfWeek(canvas)
        drawDays(canvas)
    }

    private fun drawDays(canvas: Canvas) {
        var col = findDayOffset()
        val rowCenter = paddingTop + cellHeight + cellHeight / 2
        val halfLineHeight = (dayPaint.ascent() + dayPaint.descent()) / 2f
        val c = calendar.clone() as Calendar
        for (day in 0 until DAYS_IN_WEEK) {
            val colCenter = cellWidth * col + cellWidth / 2f + paddingStart
            drawDayWithEvents(canvas, c, colCenter, rowCenter - halfLineHeight, halfLineHeight)
            col++
            c.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    fun setWeekParams(month: Int, year: Int, daysOffset: Int, weekStart: Int) {
        calendar.set(year, month, 1)
        calendar.add(Calendar.DAY_OF_MONTH, daysOffset)
        dayOfWeekStart = calendar.get(Calendar.DAY_OF_WEEK)
        this.weekStart = weekStart
        val today = DateHelper.getZeroHourCalendar()
        val c = calendar.clone() as Calendar
        for (day in 0 until DAYS_IN_WEEK) {
            if (c == today) {
                now = c.get(Calendar.DAY_OF_MONTH)
                break
            }
            c.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    override fun getDayAtLocation(x: Int, y: Int): Calendar? {
        val paddedX = x - paddingLeft
        if (paddedX < 0 || paddedX >= paddedWidth) {
            return null
        }
        val headerHeight = cellHeight
        val paddedY = y - paddingTop
        if (paddedY < headerHeight || paddedY >= paddedHeight) {
            return null
        }
        val col = paddedX * DAYS_IN_WEEK / paddedWidth
        val c = calendar.clone() as Calendar
        c.add(Calendar.DAY_OF_MONTH, col)
        return c
    }

}
