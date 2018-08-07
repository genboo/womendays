package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.graphics.Canvas
import java.util.*
import android.text.format.DateFormat
import ru.spcm.apps.womendays.R
import java.text.SimpleDateFormat

class SimplyMonthView(context: Context) : CalendarPageView(context) {

    init {
        val padding = context.resources.getDimensionPixelSize(R.dimen.calendar_month_padding)
        setPadding(padding, padding, padding, padding)
    }

    override fun onDraw(canvas: Canvas) {
        drawMonth(canvas)
        drawDaysOfWeek(canvas)
        drawDays(canvas)
    }

    private fun drawMonth(canvas: Canvas) {
        canvas.drawText(monthLabel, width / 2f, paddingTop + monthLabelHeight / 2, monthPaint)
    }

    private fun drawDays(canvas: Canvas) {
        var col = findDayOffset()
        var rowCenter = paddingTop + monthLabelHeight + cellHeight + cellHeight / 2
        val halfLineHeight = (dayPaint.ascent() + dayPaint.descent()) / 2f
        val c = calendar.clone() as Calendar
        for (day in 1..daysInMonth) {
            val colCenter = cellWidth * col + cellWidth / 2f + paddingStart

            drawDayWithEvents(canvas, c, colCenter, rowCenter - halfLineHeight, halfLineHeight)

            col++
            if (col == DAYS_IN_WEEK) {
                col = 0
                rowCenter += cellHeight
            }
            c.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    fun setMonthParams(month: Int, year: Int, weekStart: Int) {
        calendar.set(year, month, 1)
        daysInMonth = getDaysInMonth(month, year)
        dayOfWeekStart = calendar.get(Calendar.DAY_OF_WEEK)
        this.weekStart = weekStart
        val today = Calendar.getInstance()
        for (day in 1..daysInMonth) {
            if (sameDay(today, day)) {
                now = day
                currentDay = day
                break
            }
        }

        updateMonthYearLabel()
    }

    private fun getDaysInMonth(month: Int, year: Int): Int =
            when (month) {
                Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
                Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
                Calendar.FEBRUARY -> if (year % 4 == 0) 29 else 28
                else -> throw IllegalArgumentException("Invalid Month")
            }

    private fun updateMonthYearLabel() {
        val format = DateFormat.getBestDateTimePattern(Locale.getDefault(), MONTH_YEAR_FORMAT)
        val formatter = SimpleDateFormat(format, Locale.getDefault())

        monthLabel = formatter.format(calendar.time).capitalize()
    }

    override fun getDayAtLocation(x: Int, y: Int): Calendar? {
        val paddedX = x - paddingStart
        if (paddedX < 0 || paddedX >= paddedWidth) {
            return null
        }
        val headerHeight = monthLabelHeight + cellHeight
        val paddedY = y - paddingTop
        if (paddedY < headerHeight || paddedY >= paddedHeight) {
            return null
        }

        val row = (paddedY - headerHeight.toInt()) / cellHeight.toInt()
        val col = paddedX * DAYS_IN_WEEK / paddedWidth
        val index = col + row * DAYS_IN_WEEK
        val day = index + 1 - findDayOffset()
        return if (!isValidDayOfMonth(day)) {
            null
        } else {
            val c = calendar.clone() as Calendar
            c.set(Calendar.DAY_OF_MONTH, day)
            c
        }
    }

    private fun isValidDayOfMonth(day: Int): Boolean {
        return day in 1..daysInMonth
    }

}
