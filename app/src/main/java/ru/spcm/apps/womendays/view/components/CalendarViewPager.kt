package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View

class CalendarViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs){

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

            val h = child.measuredHeight

            if (h > height) {
                height = h
            }
        }

        if (height != 0) {
            height = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthMeasureSpec, height)
    }

}