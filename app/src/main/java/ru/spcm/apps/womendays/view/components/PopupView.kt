package ru.spcm.apps.womendays.view.components

import android.content.Context
import android.transition.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import ru.spcm.apps.womendays.R

class PopupView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val shadow = FrameLayout(context)
    private var isAnimated = false
    private var toggleListener: (Boolean) -> Unit = {}

    private val listener = object : ExpandListener() {
        override fun onTransitionEnd(transition: Transition) {
            isAnimated = false
            transition.removeListener(this)
        }

        override fun onTransitionStart(transition: Transition) {
            isAnimated = true
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_popup, this)

        val save = findViewById<Button>(R.id.save)
        save.setOnClickListener { save.postDelayed({ toggle() }, 200) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (parent is ViewGroup) {
            val group = parent as ViewGroup
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            shadow.layoutParams = params
            shadow.background = resources.getDrawable(R.drawable.shadow, context.theme)
            shadow.visibility = View.GONE
            var index = 0
            for (i in 0 until group.childCount) {
                if (group.getChildAt(i).id == this.id) {
                    index = i
                    break
                }
            }
            group.addView(shadow, index)
            shadow.setOnClickListener { toggle() }
        }
    }

    fun toggle() {
        if (!isAnimated) {
            makeAnimation()
            if (visibility == View.VISIBLE) {
                visibility = View.GONE
                shadow.visibility = View.GONE
                toggleListener(false)
            } else {
                visibility = View.VISIBLE
                shadow.visibility = View.VISIBLE
                toggleListener(true)
            }
        }
    }

    private fun makeAnimation() {
        val transition = TransitionSet()

        val fade = Fade()
        fade.addListener(listener)
        fade.excludeTarget(this, true)

        val slide = Slide(Gravity.BOTTOM)
        slide.excludeTarget(shadow, true)

        transition.addTransition(fade)
        transition.addTransition(slide)
        TransitionManager.beginDelayedTransition(parent as ViewGroup, transition)
    }

    fun setToggleListener(listener: (Boolean) -> Unit) {
        toggleListener = listener
    }

    abstract class ExpandListener : Transition.TransitionListener {

        override fun onTransitionResume(transition: Transition) {

        }

        override fun onTransitionPause(transition: Transition) {

        }

        override fun onTransitionCancel(transition: Transition) {

        }

    }
}