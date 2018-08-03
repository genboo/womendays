package ru.spcm.apps.womendays.tools

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    private val DATE_FORMATTER_YYYY_MM_DD = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())

    fun formatYearMonthDay(date: Date): String {
        return DATE_FORMATTER_YYYY_MM_DD.format(date)
    }

    fun getZeroHourCalendar(date: Date? = null): Calendar {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        if (date != null) {
            calendar.timeInMillis = date.time
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

}