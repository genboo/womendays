package ru.spcm.apps.womendays.model.db.converters

import android.arch.persistence.room.TypeConverter
import ru.spcm.apps.womendays.tools.DateHelper
import java.util.*

object DateConverter {
    @TypeConverter
    @JvmStatic
    fun toDate(value: Long): Date {
        return DateHelper.getZeroHourCalendar(Date(value)).time
    }

    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date): Long {
        return DateHelper.getZeroHourCalendar(date).timeInMillis
    }
}