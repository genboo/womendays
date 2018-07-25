package ru.spcm.apps.womendays.view.components

import android.util.Property

/**
 * Created by gen on 25.07.2018.
 */
class RippleAlphaProperty : Property<CalendarPageView, Int>(Int::class.java, "rippleAlpha") {
    override fun get(obj: CalendarPageView): Int {
        return obj.getRippleAlpha()
    }

    override fun set(obj: CalendarPageView, value: Int) {
        obj.setRippleAlpha(value)
    }
}