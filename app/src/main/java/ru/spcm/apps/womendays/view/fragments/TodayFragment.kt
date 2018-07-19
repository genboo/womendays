package ru.spcm.apps.womendays.view.fragments

import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fragment_today.*
import ru.spcm.apps.womendays.R
import java.util.*

/**
 * Текущиий день
 * Created by gen on 18.07.2018.
 */

class TodayFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_today, container, false)
        initFragment()
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateToolbar()

        dayWidget.setDaysLeftCount(10)
    }

    override fun inject() {
        component?.inject(this)
    }

    override fun getTitle(): String {
        return getString(R.string.menu_today)
    }

}

