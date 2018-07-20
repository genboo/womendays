package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView

class CalendarGridView(context: Context, attrs: AttributeSet, defStyle: Int) : GridView(context, attrs, defStyle) {

    constructor(context: Context, attrs: AttributeSet):this(context, attrs, 0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2,
                MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}