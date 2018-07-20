package ru.spcm.apps.womendays.view.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.spcm.apps.womendays.R
import java.util.*

class CalendarDaysAdapter(context: Context, val resource: Int, dates: ArrayList<Date>, private val month: Int?) : ArrayAdapter<Date>(context, resource, dates) {

    private val inflater = LayoutInflater.from(context)
    private val now = GregorianCalendar()

    override fun getView(position: Int, contentView: View?, parent: ViewGroup): View {
        val view = contentView ?: inflater.inflate(resource, parent, false)

        val day = GregorianCalendar()
        day.time = getItem(position)

        val dayNumber = view.findViewById<TextView>(R.id.dayNumber)
        dayNumber.text = day.get(Calendar.DAY_OF_MONTH).toString()

        when {
            isNow(day) -> dayNumber.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            isCurrentMonth(day) -> dayNumber.setTextColor(ContextCompat.getColor(context, R.color.colorText))
            else -> dayNumber.setTextColor(ContextCompat.getColor(context, R.color.colorHint))
        }

        return view
    }

    private fun isCurrentMonth(day: Calendar): Boolean {
        return month == null || day.get(Calendar.MONTH) == month
    }

    private fun isNow(day: Calendar): Boolean {
        return day.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) &&
                day.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                day.get(Calendar.YEAR) == now.get(Calendar.YEAR)
    }

}