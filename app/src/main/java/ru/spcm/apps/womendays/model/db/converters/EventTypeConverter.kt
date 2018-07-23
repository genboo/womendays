package ru.spcm.apps.womendays.model.db.converters

import android.arch.persistence.room.TypeConverter
import ru.spcm.apps.womendays.model.dto.Event

object EventTypeConverter {
    @TypeConverter
    @JvmStatic
    fun toType(value: String): Event.Type {
        return Event.Type.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromType(type: Event.Type): String {
        return type.name
    }
}