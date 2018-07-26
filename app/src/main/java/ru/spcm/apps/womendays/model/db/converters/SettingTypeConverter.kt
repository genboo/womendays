package ru.spcm.apps.womendays.model.db.converters

import android.arch.persistence.room.TypeConverter
import ru.spcm.apps.womendays.model.dto.Setting

object SettingTypeConverter {
    @TypeConverter
    @JvmStatic
    fun toType(value: String): Setting.Type {
        return Setting.Type.valueOf(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromType(type: Setting.Type): String {
        return type.name
    }
}