package ru.spcm.apps.womendays.model.dto

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import ru.spcm.apps.womendays.tools.DateHelper
import java.util.*

@Entity(indices = [Index("date")])
data class Event(@PrimaryKey(autoGenerate = true) var id: Long) {

    @Ignore
    constructor(type: Type) : this(0) {
        this.type = type
        date = DateHelper.getZeroHourCalendar(date).time
    }

    var date: Date = Date()

    var type: Type = Type.MONTHLY

    var message = ""

    enum class Type {
        SEX_SAFE,
        SEX_UNSAFE,
        MONTHLY,
        MONTHLY_CONFIRMED
    }

}