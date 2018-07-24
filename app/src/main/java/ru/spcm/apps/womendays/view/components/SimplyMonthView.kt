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
import java.text.SimpleDateFormat


class SimplyMonthView(context: Context) : View(context) {

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

    private var monthLabel = ""

    private var monthLabelHeight: Float = 0f
    private var cellHeight: Float = 0f

    private val dayFormatter = NumberFormat.getIntegerInstance(Locale.getDefault())

    init {
        monthLabelHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_month_label_height).toFloat()
        cellHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_cell_height).toFloat()

        monthPaint.color = ContextCompat.getColor(context, R.color.colorText)
        monthPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_month_label_size).toFloat()
        monthPaint.textAlign = Paint.Align.CENTER
        monthPaint.isAntiAlias = true

        dayPaint.color = ContextCompat.getColor(context, R.color.colorText)
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

        updateMonthYearLabel()
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val preferredHeight = (cellHeight * (MAX_WEEKS_IN_MONTH + 2)
                + cellHeight + monthLabelHeight
                + paddingTop + paddingBottom)
        val preferredWidth = (cellWidth * DAYS_IN_WEEK + paddingStart + paddingEnd)
        val resolvedWidth = View.resolveSize(preferredWidth, widthMeasureSpec)
        val resolvedHeight = View.resolveSize(preferredHeight.toInt(), heightMeasureSpec)
        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val w = right - left
        cellWidth = w / DAYS_IN_WEEK
    }

    companion object {
        const val DAYS_IN_WEEK = 7
        const val MAX_WEEKS_IN_MONTH = 6
        const val DEFAULT_WEEK_START = Calendar.SUNDAY
        const val MONTH_YEAR_FORMAT = "MMMMy"
    }
}
