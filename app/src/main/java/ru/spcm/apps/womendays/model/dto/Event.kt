package ru.spcm.apps.womendays.model.dto

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(indices = [Index("date")])
data class Event(@PrimaryKey(autoGenerate = true) var id: Long) {

    @Ignore
    constructor(type: Type) : this(0) {
        this.type = type
    }

    var date: Date = Date()

    var type: Type = Type.SEX

    var message = ""

    enum class Type {
        SEX,
        MONTHLY
    }

}