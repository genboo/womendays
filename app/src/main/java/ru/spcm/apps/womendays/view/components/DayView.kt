package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.support.v7.widget.AppCompatCheckedTextView
import android.view.Gravity


class DayView(context: Context) : AppCompatCheckedTextView(context){
    init{
        gravity = Gravity.CENTER
    }
}