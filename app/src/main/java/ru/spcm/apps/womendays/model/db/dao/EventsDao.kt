package ru.spcm.apps.womendays.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import ru.spcm.apps.womendays.model.dto.Event

/**
 * События
 * Created by gen on 23.07.2018.
 */
@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Event): Long

    @Delete
    fun delete(item: Event)

    @Query("SELECT * FROM Event")
    fun getEvents(): List<Event>

    @Query("SELECT * FROM Event WHERE type = 'MONTHLY' ORDER BY date DESC LIMIT 1")
    fun getLastMonthly(): Event?
}
