package ru.spcm.apps.womendays.model.dto

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(indices = [Index("cache_key")])
data class Cache(@PrimaryKey
                 @ColumnInfo(name = "cache_key")
                 val cacheKey: String,
                 val expire: Long)