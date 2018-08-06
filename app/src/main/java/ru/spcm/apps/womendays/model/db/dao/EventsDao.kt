package ru.spcm.apps.womendays.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import ru.spcm.apps.womendays.model.dto.Event
import java.util.*

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<Event>)

    @Query("SELECT * FROM Event")
    fun getEvents(): List<Event>

    @Query("SELECT * FROM Event WHERE type in ('MONTHLY_CONFIRMED', 'MONTHLY') ORDER BY date")
    fun getAllMonthly(): List<Event>

    @Query("SELECT * FROM Event WHERE type = 'MONTHLY_CONFIRMED' AND date <= :date ORDER BY date DESC LIMIT 1")
    fun getLastMonthly(date: Date): Event?

    @Query("SELECT * FROM Event ORDER BY date DESC LIMIT 1")
    fun getLastEvent(): LiveData<Event>

    @Query("SELECT * FROM Event WHERE date = :date AND type='MONTHLY_CONFIRMED'")
    fun getEventMonthlyAtDay(date: Date): Event?

    @Query("DELETE FROM Event WHERE type='MONTHLY'")
    fun clearMonthly()

    @Query("SELECT * FROM Event WHERE date = :date")
    fun getEventsByDate(date: Date): LiveData<List<Event>>

}
