package ru.spcm.apps.womendays.view.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.spcm.apps.womendays.R


class CalendarPageAdapter(val context: Context) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_calendar_grid, container, false)
        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return CALENDAR_SIZE
    }


    companion object {
        const val CALENDAR_SIZE = 2000
    }
}