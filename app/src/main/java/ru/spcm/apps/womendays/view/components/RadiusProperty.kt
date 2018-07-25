package ru.spcm.apps.womendays.view.components

import android.util.Property

/**
 * Created by gen on 25.07.2018.
 */
class RadiusProperty : Property<CalendarPageView, Float>(Float::class.java, "radius") {
    override operator fun get(obj: CalendarPageView): Float {
        return obj.getRadius()
    }

    override operator fun set(obj: CalendarPageView, value: Float) {
        obj.setRadius(value)
    }
}