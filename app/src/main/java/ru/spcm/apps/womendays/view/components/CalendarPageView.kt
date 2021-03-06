package ru.spcm.apps.womendays.view.components

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.EventsData
import ru.spcm.apps.womendays.tools.DateHelper
import ru.spcm.apps.womendays.tools.Logger
import ru.spcm.apps.womendays.view.adapters.EventsPagerAdapter
import java.text.NumberFormat
import java.util.*

abstract class CalendarPageView(context: Context) : View(context) {
    internal var paddedWidth = 0
    internal var paddedHeight = 0
    private val shiftIcon: Int
    private val shiftMonthly: Int
    private val shiftCycle: Int

    internal val monthPaint = Paint()
    internal val dayPaint = Paint()
    private val cyclePaint = Paint()
    private val daysOfWeekPaint = Paint()
    private val highlightPaint = Paint()
    private val selectedDayPaint = Paint()
    private val monthlyPaint = Paint()
    private val monthlyConfirmedPaint = Paint()
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

    internal val calendar = DateHelper.getZeroHourCalendar()
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
    internal var currentDay = -1
    private var highlightRadius = 16f
    private var currentDayRadius = 16f

    private var rippleRadius = 0f
    internal var rippleAlpha = (255 * DEFAULT_RIPPLE_ALPHA).toInt()

    private var rippleAnimator = AnimatorSet()
    private val radiusProperty = RadiusProperty()
    private val circleAlphaProperty = RippleAlphaProperty()

    private var eventsData: EventsData = EventsData()
    private val heartBitmap: Bitmap
    private val heartBrokenBitmap: Bitmap

    private var dayTextSize: Float = 14f
    private var todayTextSize: Float = 16f

    private var ovulationRadius = 16f

    private var listener: (Calendar) -> Unit = { Logger.e(this::class.java.simpleName) }

    init {
        mainTextColor = ContextCompat.getColor(context, R.color.colorText)
        todayTextColor = ContextCompat.getColor(context, R.color.colorAccent)

        monthLabelHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_month_label_height).toFloat()
        shiftIcon = context.resources.getDimensionPixelSize(R.dimen.calendar_icon_shift)
        shiftMonthly = context.resources.getDimensionPixelSize(R.dimen.calendar_monthly_shift)
        shiftCycle = context.resources.getDimensionPixelSize(R.dimen.calendar_cycle_shift)
        ovulationRadius = context.resources.getDimensionPixelSize(R.dimen.calendar_ovulation_radius).toFloat()

        cellHeight = context.resources.getDimensionPixelSize(R.dimen.calendar_cell_height).toFloat()
        highlightRadius = context.resources.getDimensionPixelSize(R.dimen.calendar_highlight_radius).toFloat()
        currentDayRadius = context.resources.getDimensionPixelSize(R.dimen.calendar_current_radius).toFloat()

        monthPaint.color = mainTextColor
        monthPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_month_label_size).toFloat()
        monthPaint.textAlign = Paint.Align.CENTER
        monthPaint.isAntiAlias = true

        dayTextSize = context.resources.getDimensionPixelSize(R.dimen.calendar_day_text_size).toFloat()
        todayTextSize = context.resources.getDimensionPixelSize(R.dimen.calendar_today_text_size).toFloat()

        dayPaint.color = mainTextColor
        dayPaint.textSize = dayTextSize
        dayPaint.textAlign = Paint.Align.CENTER
        dayPaint.isAntiAlias = true

        cyclePaint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        cyclePaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_cycle_text_size).toFloat()
        cyclePaint.textAlign = Paint.Align.CENTER
        cyclePaint.isAntiAlias = true

        daysOfWeekPaint.color = mainTextColor
        daysOfWeekPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.calendar_day_text_size).toFloat()
        daysOfWeekPaint.textAlign = Paint.Align.CENTER
        daysOfWeekPaint.isAntiAlias = true

        highlightPaint.color = ContextCompat.getColor(context, R.color.colorShadow)
        highlightPaint.alpha = rippleAlpha
        highlightPaint.isAntiAlias = true

        selectedDayPaint.color = ContextCompat.getColor(context, R.color.colorShadowLight)
        selectedDayPaint.isAntiAlias = true

        monthlyPaint.color = ContextCompat.getColor(context, R.color.colorMonthly)
        monthlyPaint.isAntiAlias = true
        monthlyPaint.strokeWidth = context.resources.getDimensionPixelSize(R.dimen.calendar_monthly_width).toFloat()

        monthlyConfirmedPaint.color = ContextCompat.getColor(context, R.color.colorMonthlyConfirmed)
        monthlyConfirmedPaint.isAntiAlias = true
        monthlyConfirmedPaint.strokeWidth = context.resources.getDimensionPixelSize(R.dimen.calendar_monthly_width).toFloat()

        ovulationPaint.color = ContextCompat.getColor(context, R.color.colorOvulation)
        ovulationPaint.isAntiAlias = true
        ovulationPaint.strokeWidth = context.resources.getDimensionPixelSize(R.dimen.calendar_ovulation_width).toFloat()
        ovulationPaint.style = Paint.Style.STROKE

        val drawableHeart = ContextCompat.getDrawable(context, R.drawable.ic_heart)
        if (drawableHeart != null) {
            heartBitmap = Bitmap.createBitmap(cellHeight.toInt() - shiftIcon, cellHeight.toInt() - shiftIcon, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(heartBitmap)
            drawableHeart.setBounds(0, 0, canvas.width, canvas.height)
            drawableHeart.setTint(ContextCompat.getColor(context, R.color.colorMonthly))
            drawableHeart.draw(canvas)
        } else {
            heartBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

        val drawableHeartBroken = ContextCompat.getDrawable(context, R.drawable.ic_heart_broken)
        if (drawableHeartBroken != null) {
            heartBrokenBitmap = Bitmap.createBitmap(cellHeight.toInt() - shiftIcon, cellHeight.toInt() - shiftIcon, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(heartBrokenBitmap)
            drawableHeartBroken.setBounds(0, 0, canvas.width, canvas.height)
            drawableHeartBroken.setTint(ContextCompat.getColor(context, R.color.colorMonthly))
            drawableHeartBroken.draw(canvas)
        } else {
            heartBrokenBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }

    }

    /**
     * Draw one day with highlight and events
     */
    fun drawDayWithEvents(canvas: Canvas, c: Calendar, x: Float, y: Float, halfLineHeight: Float) {
        val day = c.get(Calendar.DAY_OF_MONTH)

        if(day == currentDay){
            canvas.drawCircle(x, y + halfLineHeight, currentDayRadius, selectedDayPaint)
        }

        if (day == now) {
            dayPaint.color = todayTextColor
            dayPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            dayPaint.textSize = todayTextSize
        } else {
            dayPaint.color = mainTextColor
            dayPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            dayPaint.textSize = dayTextSize
        }

        val flag: Int = eventsData.events[c.timeInMillis] ?: 0
        if (flag != 0) {
            if (flag and EventsPagerAdapter.FLAG_SEX_UNSAFE > 0) {
                canvas.drawBitmap(heartBrokenBitmap, x - heartBitmap.width / 2, y - cellHeight / 2, monthlyPaint)
            }
            if(flag and EventsPagerAdapter.FLAG_SEX_SAFE > 0){
                canvas.drawBitmap(heartBitmap, x - heartBitmap.width / 2, y - cellHeight / 2, monthlyPaint)
            }

            if (flag and EventsPagerAdapter.FLAG_MONTHLY > 0 && flag and EventsPagerAdapter.FLAG_MONTHLY_CONFIRMED == 0) {
                canvas.drawLine(x - cellWidth / 2, y + shiftMonthly, x + cellWidth / 2, y + shiftMonthly, monthlyPaint)
            }

            if (flag and EventsPagerAdapter.FLAG_MONTHLY_CONFIRMED > 0) {
                canvas.drawLine(x - cellWidth / 2, y + shiftMonthly, x + cellWidth / 2, y + shiftMonthly, monthlyConfirmedPaint)
                dayPaint.color = monthlyConfirmedPaint.color
            }

            if (flag and EventsPagerAdapter.FLAG_OVULATION > 0) {
                canvas.drawCircle(x, y + halfLineHeight, ovulationRadius, ovulationPaint)
            }
        }

        val cycleDay = eventsData.cycle[c.timeInMillis] ?: 0
        if (cycleDay != 0) {
            canvas.drawText(dayFormatter.format(cycleDay), x + shiftCycle - halfLineHeight, y - shiftCycle, cyclePaint)
        }
        canvas.drawText(dayFormatter.format(day), x, y, dayPaint)

        if (day == highlightedDay) {
            canvas.drawCircle(x, y + halfLineHeight, rippleRadius, highlightPaint)
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
                if (action == MotionEvent.ACTION_DOWN && touchedItem == null) {
                    // Touch something that's not an item, reject event.
                    return false
                }
            }
            MotionEvent.ACTION_UP -> {
                val date = getDayAtLocation(x, y)
                if (date != null) {
                    val clickedDay = date.get(Calendar.DAY_OF_MONTH)
                    onDayClicked(date)
                    if (highlightedDay != clickedDay) {
                        highlightedDay = clickedDay
                        currentDay = clickedDay
                        createAnimation()
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                invalidate()
            }
        }

        return true
    }

    private fun onDayClicked(day: Calendar) {
        listener(day)
    }

    abstract fun getDayAtLocation(x: Int, y: Int): Calendar?

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

    fun setEvents(events: EventsData) {
        eventsData = events
    }

    fun setOnDayClickListener(listener: (Calendar) -> Unit) {
        this.listener = listener
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