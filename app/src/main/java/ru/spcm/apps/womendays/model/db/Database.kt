package ru.spcm.apps.womendays.model.db


import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import ru.spcm.apps.womendays.model.db.converters.DateConverter
import ru.spcm.apps.womendays.model.db.converters.EventTypeConverter
import ru.spcm.apps.womendays.model.db.converters.SettingTypeConverter
import ru.spcm.apps.womendays.model.db.dao.EventsDao
import ru.spcm.apps.womendays.model.db.dao.SettingsDao
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.model.dto.Setting

/**
 * База данных
 * Created by gen on 18.07.2018.
 */
@Database(version = 1, exportSchema = false, entities = [
    Event::class,
    Setting::class
])
@TypeConverters(EventTypeConverter::class,
        SettingTypeConverter::class,
        DateConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun eventsDao(): EventsDao
    abstract fun settingsDao(): SettingsDao
}
