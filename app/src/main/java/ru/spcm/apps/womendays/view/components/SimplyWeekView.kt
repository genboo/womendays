package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.view.View
import ru.spcm.apps.womendays.R
import java.text.NumberFormat
import java.util.*
import android.view.MotionEvent
import android.animation.AnimatorSet
import android.view.animation.AccelerateInterpolator
import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import android.util.Property
import android.animation.Animator
import android.animation.AnimatorListenerAdapter


class SimplyWeekView(context: Context) : View(context) {

    private var paddedWidth = 0
    private var paddedHeight = 0

    private val monthPaint = Paint()
    private val dayPaint = Paint()
    private val highlightPaint = Paint()
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
    private var weekStart = DEFAULT_WEEK_START
    private var dayOfWeekStart = 0

    private var now = -1

    private var cellHeight: Float = 0f

    private val dayFormatter = NumberFormat.getIntegerInstance(Locale.getDefault())

    private var mainTextColor: Int = 0
    private var todayTextColor: Int = 0

    private var highlightedDay = -1
    private var previouslyHighlightedDay = -1
    private var highlightRadius = 16f

    private var rippleRadius = 0f
    private var rippleAlpha = (255 * DEFAULT_RIPPLE_ALPHA).toInt()

    private var rippleAnimator = AnimatorSet()
    private val radiusProperty = object : Property<SimplyWeekView, Float>(Float::class.java, "radius") {
        override operator fun get(obj: SimplyWeekView): Float {
            return obj.getRadius()
        }

        override operator fun set(obj: SimplyWeekView, value: Float) {
            obj.setRadius(value)
        }
    }
    private val circleAlphaProperty = object : Property<SimplyWeekView, Int>(Int::class.java, "rippleAlpha") {
        override fun get(obj: SimplyWeekView): Int {
            return obj.getRippleAlpha()
        }

        override fun set(obj: SimplyWeekView, value: Int) {
            obj.setRippleAlpha(value)
        }
    }

    init {

        mainTextColor = ContextCompat.getColor(context, R.color.colorText)
        todayTextColor = ContextCompat.getColor(context, R.color.colorAccent)

        cellHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_cell_height).toFloat()
        highlightRadius = context.resources.getDimensionPixelSize(R.dimen.calendar_highlight_radius).toFloat()
        val padding = context.resources.getDimensionPixelSize(R.dimen.calendar_month_padding)
        setPadding(padding, padding, padding, padding)

        monthPaint.color = mainTextColor
        monthPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_month_label_size).toFloat()
        monthPaint.textAlign = Paint.Align.CENTER
        monthPaint.isAntiAlias = true

        dayPaint.color = mainTextColor
        dayPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_day_text_size).toFloat()
        dayPaint.textAlign = Paint.Align.CENTER
        dayPaint.isAntiAlias = true

        highlightPaint.color = ContextCompat.getColor(context, R.color.colorShadow)
        highlightPaint.isAntiAlias = true

        if (isInEditMode) {
            highlightedDay = 1
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawDaysOfWeek(canvas)
        drawDays(canvas)
    }

    private fun drawDaysOfWeek(canvas: Canvas) {
        for (col in 0 until DAYS_IN_WEEK) {
            val label = dayOfWeekLabels[col]
            val colCenter = cellWidth * col + cellWidth / 2f
            canvas.drawText(label, colCenter, paddingTop + cellHeight / 2, dayPaint)
        }
    }

    private fun drawDays(canvas: Canvas) {
        var col = findDayOffset()
        val rowCenter = paddingTop + cellHeight + cellHeight / 2
        val halfLineHeight = (dayPaint.ascent() + dayPaint.descent()) / 2f
        val c = calendar.clone() as Calendar
        for (day in 0 until DAYS_IN_WEEK) {
            val colCenter = cellWidth * col + cellWidth / 2f
            if (c.get(Calendar.DAY_OF_MONTH) == highlightedDay) {
                canvas.drawCircle(colCenter, rowCenter, rippleRadius, highlightPaint)
            }

            if (c.get(Calendar.DAY_OF_MONTH) == now) {
                dayPaint.color = todayTextColor
            } else {
                dayPaint.color = mainTextColor
            }

            canvas.drawText(dayFormatter.format(c.get(Calendar.DAY_OF_MONTH)), colCenter, rowCenter - halfLineHeight, dayPaint)
            col++
            c.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    fun setWeekParams(month: Int, year: Int, daysOffset: Int, weekStart: Int) {
        calendar.set(year, month, 1)
        calendar.add(Calendar.DAY_OF_MONTH, daysOffset)
        dayOfWeekStart = calendar.get(Calendar.DAY_OF_WEEK)
        this.weekStart = weekStart
        val today = Calendar.getInstance()
        val c =  calendar.clone() as Calendar
        for (day in  0 until DAYS_IN_WEEK) {
            if (sameDay(today, c.get(Calendar.DAY_OF_MONTH))) {
                now = c.get(Calendar.DAY_OF_MONTH)
                break
            }
            c.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun sameDay(today: Calendar, day: Int): Boolean {
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && day == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun findDayOffset(): Int {
        val offset = dayOfWeekStart - weekStart
        return if (dayOfWeekStart < weekStart) {
            offset + DAYS_IN_WEEK
        } else {
            offset
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = (event.x + 0.5f).toInt()
        val y = (event.y + 0.5f).toInt()
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val touchedItem = getDayAtLocation(x, y)
                if (action == MotionEvent.ACTION_DOWN && touchedItem < 0) {
                    // Touch something that's not an item, reject event.
                    return false
                }
            }
            MotionEvent.ACTION_UP -> {
                val clickedDay = getDayAtLocation(x, y)
                onDayClicked(clickedDay)
                if (highlightedDay != clickedDay) {
                    highlightedDay = clickedDay
                    previouslyHighlightedDay = clickedDay
                    createAnimation()
                    invalidate()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                invalidate()
            }
        }

        return true
    }

    private fun onDayClicked(day: Int) {

    }

    private fun getDayAtLocation(x: Int, y: Int): Int {
        val paddedX = x - paddingLeft
        if (paddedX < 0 || paddedX >= paddedWidth) {
            return -1
        }
        val headerHeight = cellHeight
        val paddedY = y - paddingTop
        if (paddedY < headerHeight || paddedY >= paddedHeight) {
            return -1
        }
        val col = paddedX * DAYS_IN_WEEK / paddedWidth
        val c  = calendar.clone() as Calendar
        c.add(Calendar.DAY_OF_MONTH, col)
        return c.get(Calendar.DAY_OF_MONTH)
    }

    private fun createAnimation() {
        cancelAnimations()
        rippleAnimator = AnimatorSet()
        rippleAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                setRadius(0f)
                setRippleAlpha(rippleAlpha)
                highlightedDay = -1
            }
        })
        val ripple = ObjectAnimator.ofFloat<SimplyWeekView>(this, radiusProperty, 0f, highlightRadius)
        ripple.duration = DEFAULT_HIGHLIGHT_SPEED
        ripple.interpolator = DecelerateInterpolator()

        val fade = ObjectAnimator.ofInt<SimplyWeekView>(this, circleAlphaProperty, rippleAlpha, 0)
        fade.duration = DEFAULT_HIGHLIGHT_SPEED
        fade.interpolator = AccelerateInterpolator()
        fade.startDelay = DEFAULT_HIGHLIGHT_SPEED / 2


        rippleAnimator.playTogether(ripple, fade)
        rippleAnimator.start()
    }

    private fun cancelAnimations() {
        rippleAnimator.cancel()
        rippleAnimator.removeAllListeners()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val preferredHeight = (cellHeight + cellHeight + paddingTop + paddingBottom)
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


    private fun getRadius(): Float {
        return rippleRadius
    }


    fun setRadius(radius: Float) {
        rippleRadius = radius
        invalidate()
    }

    fun getRippleAlpha(): Int {
        return highlightPaint.alpha
    }

    fun setRippleAlpha(rippleAlpha: Int) {
        highlightPaint.alpha = rippleAlpha
        invalidate()
    }

    companion object {
        const val DAYS_IN_WEEK = 7
        const val DEFAULT_WEEK_START = Calendar.SUNDAY
        const val DEFAULT_HIGHLIGHT_SPEED = 150L
        const val DEFAULT_RIPPLE_ALPHA = 0.2f
    }
}
