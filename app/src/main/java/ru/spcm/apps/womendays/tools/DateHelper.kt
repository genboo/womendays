package ru.spcm.apps.womendays.tools

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    private val DATE_FORMATTER_YYYY_MM_DD = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())

    fun formatYearMonthDay(date: Date): String {
        return DATE_FORMATTER_YYYY_MM_DD.format(date)
    }
}