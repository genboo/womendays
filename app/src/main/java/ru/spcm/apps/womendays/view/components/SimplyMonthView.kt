package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.view.View
import ru.spcm.apps.womendays.R
import java.text.NumberFormat
import java.util.*
import android.text.format.DateFormat
import android.view.MotionEvent
import ru.spcm.apps.womendays.tools.Logger
import java.text.SimpleDateFormat


class SimplyMonthView(context: Context) : View(context) {

    private var paddedWidth = 0
    private var paddedHeight = 0

    private val monthPaint = Paint()
    private val dayPaint = Paint()
    private val dayOfWeekLabels = arrayOf(
            context.getString(R.string.calendar_monday),
            context.getString(R.string.calendar_tuesday),
            context.getString(R.string.calendar_wednesday),
            context.getString(R.string.calendar_thursday),
            context.getString(R.string.calendar_friday),
            context.getString(R.string.calendar_saturday),
            context.getString(R.string.calendar_sunday)
    )

    private var cellWidth: Int = 0

    private val calendar = Calendar.getInstance()
    private var daysInMonth = 31
    private var weekStart = DEFAULT_WEEK_START
    private var dayOfWeekStart = 0

    private var now = 0

    private var monthLabel = ""

    private var monthLabelHeight: Float = 0f
    private var cellHeight: Float = 0f

    private val dayFormatter = NumberFormat.getIntegerInstance(Locale.getDefault())

    private var mainTextColor: Int = 0
    private var todayTextColor: Int = 0

    init {

        mainTextColor = ContextCompat.getColor(context, R.color.colorText)
        todayTextColor = ContextCompat.getColor(context, R.color.colorAccent)

        monthLabelHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_month_label_height).toFloat()
        cellHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_cell_height).toFloat()

        monthPaint.color = mainTextColor
        monthPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_month_label_size).toFloat()
        monthPaint.textAlign = Paint.Align.CENTER
        monthPaint.isAntiAlias = true

        dayPaint.color = mainTextColor
        dayPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_day_text_size).toFloat()
        dayPaint.textAlign = Paint.Align.CENTER
        dayPaint.isAntiAlias = true


    }

    override fun onDraw(canvas: Canvas) {
        drawMonth(canvas)
        drawDaysOfWeek(canvas)
        drawDays(canvas)
    }

    private fun drawMonth(canvas: Canvas) {
        canvas.drawText(monthLabel, width / 2f, 32f, monthPaint)
    }

    private fun drawDaysOfWeek(canvas: Canvas) {
        for (col in 0 until DAYS_IN_WEEK) {
            val label = dayOfWeekLabels[col]
            val colCenter = cellWidth * col + cellWidth / 2f
            canvas.drawText(label, colCenter, monthLabelHeight + cellHeight, dayPaint)
        }
    }

    private fun drawDays(canvas: Canvas) {
        var col = findDayOffset()
        var rowCenter = monthLabelHeight + cellHeight * 2
        for (day in 1..daysInMonth) {
            val colCenter = cellWidth * col + cellWidth / 2f

            if (now == day) {
                dayPaint.color = todayTextColor
            } else {
                dayPaint.color = mainTextColor
            }

            canvas.drawText(dayFormatter.format(day), colCenter, rowCenter, dayPaint)

            col++
            if (col == DAYS_IN_WEEK) {
                col = 0
                rowCenter += cellHeight
            }
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
            }
        }

        updateMonthYearLabel()
    }

    private fun sameDay(today: Calendar, day: Int): Boolean {
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && day == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun getDaysInMonth(month: Int, year: Int): Int =
            when (month) {
                Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
                Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
                Calendar.FEBRUARY -> if (year % 4 == 0) 29 else 28
                else -> throw IllegalArgumentException("Invalid Month")
            }

    private fun findDayOffset(): Int {
        val offset = dayOfWeekStart - weekStart
        return if (dayOfWeekStart < weekStart) {
            offset + DAYS_IN_WEEK
        } else {
            offset
        }
    }

    private fun updateMonthYearLabel() {
        val format = DateFormat.getBestDateTimePattern(Locale.getDefault(), MONTH_YEAR_FORMAT)
        val formatter = SimpleDateFormat(format, Locale.getDefault())

        monthLabel = formatter.format(calendar.time).capitalize()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = (event.x + 0.5f).toInt()
        val y = (event.y + 0.5f).toInt()
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val touchedItem = getDayAtLocation(x, y)
                Logger.e("day : $touchedItem")
//                mIsTouchHighlighted = true
//                if (mHighlightedDay !== touchedItem) {
//                    mHighlightedDay = touchedItem
//                    mPreviouslyHighlightedDay = touchedItem
//                    invalidate()
//                }
                if (action == MotionEvent.ACTION_DOWN && touchedItem < 0) {
                    // Touch something that's not an item, reject event.
                    return false
                }
            }
            MotionEvent.ACTION_UP -> {
                val clickedDay = getDayAtLocation(x, y)
//                onDayClicked(clickedDay)
//                // Reset touched day on stream end.
//                mHighlightedDay = -1
//                mIsTouchHighlighted = false
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
//                mHighlightedDay = -1
//                mIsTouchHighlighted = false
                invalidate()
            }
        }

        return true
    }

    private fun getDayAtLocation(x: Int, y: Int): Int {
        val paddedX = x - paddingLeft
        if (paddedX < 0 || paddedX >= paddedWidth) {
            return -1
        }
        val headerHeight = monthLabelHeight + cellHeight
        val paddedY = y - paddingTop
        if (paddedY < headerHeight || paddedY >= paddedHeight) {
            return -1
        }

        val row = (paddedY - headerHeight.toInt()) / cellHeight.toInt()
        val col = paddedX * DAYS_IN_WEEK / paddedWidth
        val index = col + row * DAYS_IN_WEEK
        val day = index + 1 - findDayOffset()
        return if (!isValidDayOfMonth(day)) {
            -1
        } else {
            day
        }
    }

    private fun isValidDayOfMonth(day: Int): Boolean {
        return day in 1..daysInMonth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val preferredHeight = (cellHeight * MAX_WEEKS_IN_MONTH
                + cellHeight + monthLabelHeight
                + paddingTop + paddingBottom)
        val preferredWidth = (cellWidth * DAYS_IN_WEEK + paddingStart + paddingEnd)
        val resolvedWidth = resolveSize(preferredWidth, widthMeasureSpec)
        val resolvedHeight = resolveSize(preferredHeight.toInt(), heightMeasureSpec)
        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val w = right - left
        val h = bottom - top

        cellWidth = w / DAYS_IN_WEEK

        paddedWidth = w - paddingEnd - paddingStart
        paddedHeight = h - paddingBottom - paddingTop
    }

    companion object {
        const val DAYS_IN_WEEK = 7
        const val MAX_WEEKS_IN_MONTH = 6
        const val DEFAULT_WEEK_START = Calendar.SUNDAY
        const val MONTH_YEAR_FORMAT = "MMMMy"
    }
}
