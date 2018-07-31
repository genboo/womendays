package ru.spcm.apps.womendays.view.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fragment_today.*
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.model.dto.TodayData
import ru.spcm.apps.womendays.view.components.fadeIn
import ru.spcm.apps.womendays.view.components.fadeOut
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
        viewModel.data.observe(this, Observer { observeTodayData(it) })
        viewModel.eventsObserver.observe(this, Observer { viewModel.loadEvents() })

        getFab().setOnClickListener {
            viewModel.save(Event.Type.SEX_SAFE).observe(this, Observer { id ->
                showSnack(R.string.action_added, View.OnClickListener {
                    viewModel.delete(id)
                })
            })
        }

        dayWidget.setAddMonthlyListener(View.OnClickListener {
            viewModel.save(Event.Type.MONTHLY).observe(this, Observer { id ->
                showSnack(R.string.action_added, View.OnClickListener {
                    viewModel.delete(id)
                })
            })
        })
    }

    private fun observeTodayData(data: TodayData?) {
        if (data != null) {
            dayWidget.setData(data)
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
        getFab().fadeIn()
    }

    override fun onPause() {
        super.onPause()
        getFab().fadeOut()
    }
}

