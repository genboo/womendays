package ru.spcm.apps.womendays.model.db


import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import ru.spcm.apps.womendays.model.dto.Cache

/**
 * База данных
 * Created by gen on 18.07.2018.
 */
@Database(version = 1, exportSchema = false, entities = [
    Cache::class
])
abstract class Database : RoomDatabase() {

}
