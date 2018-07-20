package ru.spcm.apps.womendays.view.components

import android.support.v4.view.ViewPager
import android.widget.TextView
import ru.spcm.apps.womendays.R
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class CalendarOnPageChangeListener(private val textView: TextView, private val calendar: Calendar)
    : ViewPager.OnPageChangeListener {

    private val dateFormatSymbols = object : DateFormatSymbols() {

        override fun getMonths(): Array<String> {
            val context = textView.context

            return arrayOf(context.getString(R.string.calendar_january),
                    context.getString(R.string.calendar_february),
                    context.getString(R.string.calendar_march),
                    context.getString(R.string.calendar_april),
                    context.getString(R.string.calendar_may),
                    context.getString(R.string.calendar_june),
                    context.getString(R.string.calendar_july),
                    context.getString(R.string.calendar_august),
                    context.getString(R.string.calendar_september),
                    context.getString(R.string.calendar_october),
                    context.getString(R.string.calendar_november),
                    context.getString(R.string.calendar_december))
        }

    }

    private val dateFormat = SimpleDateFormat("MMMM, y", Locale.getDefault())

    init {
        if (Locale.getDefault() == Locale("ru", "RU")) {
            dateFormat.dateFormatSymbols = dateFormatSymbols
        }
    }

    override fun onPageSelected(position: Int) {
        val calendar = calendar.clone() as Calendar
        calendar.add(Calendar.MONTH, position)
        textView.text = dateFormat.format(calendar.time)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }


}