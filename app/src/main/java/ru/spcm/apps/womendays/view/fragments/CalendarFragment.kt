package ru.spcm.apps.womendays.view.fragments

import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.view.adapters.CalendarPageAdapter
import java.util.*

/**
 * Календарь
 * Created by gen on 18.07.2018.
 */

class CalendarFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        initFragment()
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateToolbar()

        calendar.adapter = CalendarPageAdapter(requireContext())

    }

    override fun inject() {
        component?.inject(this)
    }

    override fun getTitle(): String {
        return getString(R.string.menu_calendar)
    }

}

