package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import ru.spcm.apps.womendays.R
import java.text.SimpleDateFormat
import java.util.*

class DayView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val backgroundPaint = Paint()

    private var daysCounter: TextView
    private var todayLabel: TextView

    private var daysLeftCount: Int = 0

    init {
        setWillNotDraw(false)

        backgroundPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        backgroundPaint.isAntiAlias = true
        backgroundPaint.style = Paint.Style.FILL_AND_STROKE

        LayoutInflater.from(context).inflate(R.layout.layout_today, this)

        daysCounter = findViewById(R.id.daysLeftCount)
        daysCounter.text = "$daysLeftCount"

        todayLabel = findViewById(R.id.today)

        val dateFormatter = SimpleDateFormat("E, dd MMMM", Locale.getDefault())
        todayLabel.text = dateFormatter.format(Date())

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(width / 2f, height / 2f, height / 2f, backgroundPaint)
    }

    fun setDaysLeftCount(count: Int) {
        daysLeftCount = count
        daysCounter.text = "$daysLeftCount"
    }

}