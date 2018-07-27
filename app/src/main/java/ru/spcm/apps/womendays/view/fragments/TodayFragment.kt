package ru.spcm.apps.womendays.view.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fragment_today.*
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.view.components.slideIn
import ru.spcm.apps.womendays.view.components.slideOut
import ru.spcm.apps.womendays.viewmodel.DayViewModel

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

        val viewModel = getViewModel(this, DayViewModel::class.java)
        viewModel.events.observe(this, Observer { observeEvents(it) })
        calendarView.postDelayed({ viewModel.loadEvents() }, 200)

        dayWidget.setDaysLeftCount(10)

        getFab().setOnClickListener {
            viewModel.save(Event.Type.SEX_SAFE).observe(this, Observer { id ->
                showSnack(R.string.action_added, View.OnClickListener {
                    viewModel.delete(id)
                })
            })
        }
    }

    private fun observeEvents(data: HashMap<String, Int>?) {
        if (data != null) {
            calendarView.setEvents(data)
        }
    }

    override fun inject() {
        component?.inject(this)
    }

    override fun getTitle(): String {
        return getString(R.string.menu_today)
    }

    override fun onResume() {
        super.onResume()
        getFab().slideIn(Gravity.END)
    }

    override fun onPause() {
        super.onPause()
        getFab().slideOut(Gravity.END)
    }
}

