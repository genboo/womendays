package ru.spcm.apps.womendays.model.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import ru.spcm.apps.womendays.model.dto.Setting

/**
 * Настройки
 * Created by gen on 23.07.2018.
 */
@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Setting)

    @Query("SELECT * FROM Setting")
    fun getSettings(): LiveData<List<Setting>>

    @Query("SELECT value FROM Setting WHERE type = :type")
    fun getSettingValueString(type: String): String

    @Query("SELECT value FROM Setting WHERE type = :type")
    fun getSettingValueInt(type: String): Int
}
