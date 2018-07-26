package ru.spcm.apps.womendays.model.dto

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Setting(@PrimaryKey var type: Type, var value: String = "") {

    enum class Type {
        LENGTH,
        PERIOD
    }

}