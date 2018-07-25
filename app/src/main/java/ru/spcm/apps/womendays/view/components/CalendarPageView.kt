package ru.spcm.apps.womendays.view.components

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.tools.Logger
import java.text.NumberFormat
import java.util.*

abstract class CalendarPageView(context: Context) : View(context) {
    internal var paddedWidth = 0
    internal var paddedHeight = 0

    internal val monthPaint = Paint()
    internal val dayPaint = Paint()
    internal val highlightPaint = Paint()
    private val dayOfWeekLabels = arrayOf(
            context.getString(R.string.calendar_monday),
            context.getString(R.string.calendar_tuesday),
            context.getString(R.string.calendar_wednesday),
            context.getString(R.string.calendar_thursday),
            context.getString(R.string.calendar_friday),
            context.getString(R.string.calendar_saturday),
            context.getString(R.string.calendar_sunday)
    )

    internal var cellWidth: Int = 0

    internal val calendar = Calendar.getInstance()
    internal var daysInMonth = 31
    internal var weekStart = DEFAULT_WEEK_START
    internal var dayOfWeekStart = 0
    internal var weekInMonth = MAX_WEEKS_IN_MONTH

    internal var now = 0

    internal var monthLabel = ""

    internal var monthLabelHeight: Float = 0f
    internal var cellHeight: Float = 0f

    internal val dayFormatter = NumberFormat.getIntegerInstance(Locale.getDefault())

    internal var mainTextColor: Int = 0
    internal var todayTextColor: Int = 0

    internal var highlightedDay = -1
    private var previouslyHighlightedDay = -1
    private var highlightRadius = 16f

    internal var rippleRadius = 0f
    internal var rippleAlpha = (255 * DEFAULT_RIPPLE_ALPHA).toInt()

    private var rippleAnimator = AnimatorSet()
    private val radiusProperty = RadiusProperty()
    private val circleAlphaProperty = RippleAlphaProperty()

    init {
        mainTextColor = ContextCompat.getColor(context, R.color.colorText)
        todayTextColor = ContextCompat.getColor(context, R.color.colorAccent)

        monthLabelHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_month_label_height).toFloat()
        cellHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_cell_height).toFloat()
        highlightRadius = context.resources.getDimensionPixelSize(R.dimen.calendar_highlight_radius).toFloat()

        monthPaint.color = mainTextColor
        monthPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_month_label_size).toFloat()
        monthPaint.textAlign = Paint.Align.CENTER
        monthPaint.isAntiAlias = true

        dayPaint.color = mainTextColor
        dayPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_day_text_size).toFloat()
        dayPaint.textAlign = Paint.Align.CENTER
        dayPaint.isAntiAlias = true

        highlightPaint.color = ContextCompat.getColor(context, R.color.colorShadow)
        highlightPaint.alpha = rippleAlpha
        highlightPaint.isAntiAlias = true

        if (isInEditMode) {
            highlightedDay = 1
        }
    }

    internal fun drawDaysOfWeek(canvas: Canvas) {
        for (col in 0 until DAYS_IN_WEEK) {
            val label = dayOfWeekLabels[col]
            val colCenter = cellWidth * col + cellWidth / 2f + paddingStart
            canvas.drawText(label, colCenter, paddingTop + monthLabelHeight + cellHeight / 2, dayPaint)
        }
    }

    internal fun sameDay(today: Calendar, day: Int): Boolean {
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && day == today.get(Calendar.DAY_OF_MONTH)
    }

    internal fun findDayOffset(): Int {
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
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
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
        Logger.e("clicked day: $day")
    }

    abstract fun getDayAtLocation(x: Int, y: Int): Int

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
        val ripple = ObjectAnimator.ofFloat<CalendarPageView>(this, radiusProperty, 0f, highlightRadius)
        ripple.duration = DEFAULT_HIGHLIGHT_SPEED
        ripple.interpolator = DecelerateInterpolator()

        val fade = ObjectAnimator.ofInt<CalendarPageView>(this, circleAlphaProperty, rippleAlpha, 0)
        fade.duration = DEFAULT_HIGHLIGHT_SPEED / 2
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
        val preferredHeight = (cellHeight * weekInMonth
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

        paddedWidth = w - paddingEnd - paddingStart
        paddedHeight = h - paddingBottom - paddingTop

        cellWidth = paddedWidth / DAYS_IN_WEEK
    }


    fun getRadius(): Float {
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
        const val MAX_WEEKS_IN_MONTH = 6
        const val DEFAULT_WEEK_START = Calendar.SUNDAY
        const val MONTH_YEAR_FORMAT = "MMMMy"
        const val DEFAULT_HIGHLIGHT_SPEED = 200L
        const val DEFAULT_RIPPLE_ALPHA = 0.1f
    }
}