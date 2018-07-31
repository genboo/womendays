package ru.spcm.apps.womendays.view.components

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.tools.DateHelper
import ru.spcm.apps.womendays.tools.Logger
import ru.spcm.apps.womendays.view.adapters.EventsPagerAdapter
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap

abstract class CalendarPageView(context: Context) : View(context) {
    internal var paddedWidth = 0
    internal var paddedHeight = 0
    private val shiftIcon: Int
    private val shiftMonthly: Int

    internal val monthPaint = Paint()
    internal val dayPaint = Paint()
    private val daysOfWeekPaint = Paint()
    private val highlightPaint = Paint()
    private val monthlyPaint = Paint()
    private val ovulationPaint = Paint()
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

    private val dayFormatter = NumberFormat.getIntegerInstance(Locale.getDefault())

    private var mainTextColor: Int = 0
    private var todayTextColor: Int = 0

    internal var highlightedDay = -1
    private var previouslyHighlightedDay = -1
    private var highlightRadius = 16f

    private var rippleRadius = 0f
    internal var rippleAlpha = (255 * DEFAULT_RIPPLE_ALPHA).toInt()

    private var rippleAnimator = AnimatorSet()
    private val radiusProperty = RadiusProperty()
    private val circleAlphaProperty = RippleAlphaProperty()

    private var events: HashMap<String, Int> = HashMap()
    private val heartBitmap: Bitmap

    private var ovulationRadius = 16f

    init {
        mainTextColor = ContextCompat.getColor(context, R.color.colorText)
        todayTextColor = ContextCompat.getColor(context, R.color.colorAccent)

        monthLabelHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_month_label_height).toFloat()
        val iconSize = context.resources.getDimensionPixelSize(R.dimen.calendar_icon_size)
        shiftIcon = context.resources.getDimensionPixelSize(R.dimen.calendar_icon_shift)
        shiftMonthly = context.resources.getDimensionPixelSize(R.dimen.calendar_monthly_shift)
        ovulationRadius = context.resources.getDimensionPixelSize(R.dimen.calendar_ovulation_radius).toFloat()

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

        daysOfWeekPaint.color = mainTextColor
        daysOfWeekPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_day_text_size).toFloat()
        daysOfWeekPaint.textAlign = Paint.Align.CENTER
        daysOfWeekPaint.isAntiAlias = true

        highlightPaint.color = ContextCompat.getColor(context, R.color.colorShadow)
        highlightPaint.alpha = rippleAlpha
        highlightPaint.isAntiAlias = true

        monthlyPaint.color = ContextCompat.getColor(context, R.color.colorMonthly)
        monthlyPaint.isAntiAlias = true
        monthlyPaint.strokeWidth = context.resources.getDimensionPixelSize(R.dimen.calendar_monthly_width).toFloat()

        ovulationPaint.color = ContextCompat.getColor(context, R.color.colorOvulation)
        ovulationPaint.isAntiAlias = true
        ovulationPaint.strokeWidth = context.resources.getDimensionPixelSize(R.dimen.calendar_ovulation_width).toFloat()
        ovulationPaint.style = Paint.Style.STROKE

        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_heart)

        if (drawable != null) {
            heartBitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(heartBitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.setTint(ContextCompat.getColor(context, R.color.colorMonthly))
            drawable.draw(canvas)
        } else {
            heartBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

    }

    /**
     * Draw one day with highlight and events
     */
    fun drawDayWithEvents(canvas: Canvas, c: Calendar, x: Float, y: Float, halfLineHeight: Float, paint: Paint) {
        val day = c.get(Calendar.DAY_OF_MONTH)

        if (day == highlightedDay) {
            canvas.drawCircle(x, y + halfLineHeight, rippleRadius, highlightPaint)
        }

        if (day == now) {
            dayPaint.color = todayTextColor
        } else {
            dayPaint.color = mainTextColor
        }

        canvas.drawText(dayFormatter.format(day), x, y, paint)

        val date = DateHelper.formatYearMonthDay(c.time)
        val flag: Int = events[date] ?: 0
        if(flag != 0) {
            if (flag and EventsPagerAdapter.FLAG_SEX_SAFE > 0) {
                canvas.drawBitmap(heartBitmap, x - cellWidth / 2, y - cellHeight / 2 + shiftIcon, monthlyPaint)
            }
            if (flag and EventsPagerAdapter.FLAG_SEX_UNSAFE > 0) {
                canvas.drawBitmap(heartBitmap, x - cellWidth / 2, y - cellHeight / 2 + shiftIcon, monthlyPaint)
                canvas.drawCircle(x - cellWidth / 2, y - cellHeight / 2 + shiftIcon, 6f, monthlyPaint)
            }
            if (flag and EventsPagerAdapter.FLAG_MONTHLY > 0 || flag and EventsPagerAdapter.FLAG_MONTHLY_CONFIRMED > 0) {
                canvas.drawLine(x - cellWidth / 2, y + shiftMonthly, x + cellWidth / 2, y + shiftMonthly, monthlyPaint)
            }
            if (flag and EventsPagerAdapter.FLAG_OVULATION > 0) {
                canvas.drawCircle(x, y + halfLineHeight, ovulationRadius, ovulationPaint)
            }
        }

    }

    /**
     * Draw line with days of week like
     * mon tue wed thu fri sat sun
     */
    internal fun drawDaysOfWeek(canvas: Canvas) {
        for (col in 0 until DAYS_IN_WEEK) {
            val label = dayOfWeekLabels[col]
            val colCenter = cellWidth * col + cellWidth / 2f + paddingStart
            canvas.drawText(label, colCenter, paddingTop + monthLabelHeight + cellHeight / 2, daysOfWeekPaint)
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

    fun setEvents(events: HashMap<String, Int>) {
        this.events = events
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