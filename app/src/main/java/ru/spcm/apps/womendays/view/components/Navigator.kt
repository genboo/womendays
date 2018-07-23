package ru.spcm.apps.womendays.view.components

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.transition.Fade
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.view.fragments.CalendarFragment
import ru.spcm.apps.womendays.view.fragments.TodayFragment
import ru.terrakok.cicerone.android.SupportFragmentNavigator
import ru.terrakok.cicerone.commands.*

class Navigator(private val activity: FragmentActivity, fragmentManager: FragmentManager, containerId: Int)
    : SupportFragmentNavigator(fragmentManager, containerId) {

    override fun createFragment(screenKey: String, data: Any?): Fragment {
        when (screenKey) {
            SCREEN_TODAY -> return TodayFragment()
            SCREEN_CALENDAR -> return CalendarFragment()
        }
        return Fragment()
    }

    override fun showSystemMessage(message: String) {

    }

    override fun exit() {
        activity.finish()
    }

    override fun setupFragmentTransactionAnimation(command: Command,
                                                   currentFragment: Fragment?,
                                                   nextFragment: Fragment?,
                                                   fragmentTransaction: FragmentTransaction) {
        if (command is Replace) {
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out)
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right_2)
        }
    }

    fun goToToday() {
        applyCommands(arrayOf(BackTo(null), Replace(SCREEN_TODAY, "")))
    }

    fun goToCalendar() {
        applyCommands(arrayOf(BackTo(null), Replace(SCREEN_CALENDAR, "")))
    }

    fun backTo() {
        applyCommand(Back())
    }

    companion object {
        const val SCREEN_TODAY = "today"
        const val SCREEN_CALENDAR = "calendar"
    }

}