package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_today.view.*
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.TodayData
import java.text.SimpleDateFormat
import java.util.*

class TodayView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val backgroundPaint = Paint()

    private var label: TextView

    private val pattern = context.getString(R.string.view_days_pattern)

    private var data: TodayData = TodayData()

    init {
        setWillNotDraw(false)

        backgroundPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        backgroundPaint.isAntiAlias = true
        backgroundPaint.style = Paint.Style.FILL_AND_STROKE

        LayoutInflater.from(context).inflate(R.layout.layout_today, this)

        label = findViewById(R.id.label)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(width / 2f, height / 2f, height / 2f, backgroundPaint)
    }

    fun setData(data: TodayData) {
        this.data = data
        updateWidget()
    }

    private fun updateWidget() {
        label.text = getLabel()
    }

    private fun getLabel(): Spannable {
        val builder = SpannableStringBuilder()

        val dateFormatter = SimpleDateFormat("E, dd MMMM", Locale.getDefault())
        val dayLabel = when (data.daysLeft) {
            1, 21, 31 -> context.getString(R.string.view_day_one)
            2, 3, 4, 22, 23, 24, 32, 33, 34 -> context.getString(R.string.view_day_three)
            else -> context.getString(R.string.view_day_two)
        }
        builder.append(String.format(pattern, dateFormatter.format(Calendar.getInstance().time).capitalize(), data.cycleDay, data.daysLeft, dayLabel))
        val textLabel = builder.toString()
        val secondLineStart = textLabel.indexOf('\n') + 1
        val thirdLineStart = textLabel.indexOf('\n', secondLineStart) + 1
        builder.setSpan(RelativeSizeSpan(0.7f), 0, secondLineStart, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(RelativeSizeSpan(1.5f), secondLineStart, thirdLineStart, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(RelativeSizeSpan(0.8f), thirdLineStart, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return builder
    }


    fun setAddMonthlyListener(listener: View.OnClickListener) {
        addMonthly.setOnClickListener(listener)
    }

}