package ru.spcm.apps.womendays.view.fragments

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.model.dto.EventsData
import ru.spcm.apps.womendays.tools.DateHelper
import ru.spcm.apps.womendays.view.adapters.EventsListAdapter
import ru.spcm.apps.womendays.view.components.slideIn
import ru.spcm.apps.womendays.view.components.slideOut
import ru.spcm.apps.womendays.viewmodel.DayViewModel

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

        val viewModel = getViewModel(this, DayViewModel::class.java)
        viewModel.events.observe(this, Observer { observeEventsData(it) })
        viewModel.eventsByDay.observe(this, Observer { observeEvents(it) })
        calendarView.postDelayed({ viewModel.loadEvents() }, 200)
        calendarView.postDelayed({ viewModel.loadEventsByDate(DateHelper.getZeroHourCalendar().time) }, 300)

        calendarView.setOnDayClickListener {
            viewModel.loadEventsByDate(it.time)
        }

        list.layoutManager = LinearLayoutManager(requireContext())
        val adapter = EventsListAdapter(null)
        list.adapter = adapter
        adapter.setOnItemClickListener { _, item, _ ->
            viewModel.delete(item.id).observe(this, Observer {
                if (it != null && it) {
                    showSnack(R.string.action_deleted, View.OnClickListener { _ ->
                        viewModel.restore(item)
                    })
                }
            })
        }

    }

    private fun observeEvents(data: List<Event>?) {
        if (data != null && data.isNotEmpty()) {
            (list.adapter as EventsListAdapter).setItems(data)
            if (contentBlock.visibility == View.GONE) {
                contentBlock.slideIn(Gravity.BOTTOM)
            }
        } else {
            contentBlock.slideOut(Gravity.BOTTOM)
            appBarCalendar.setExpanded(true)
        }
    }

    private fun observeEventsData(data: EventsData?) {
        if (data != null) {
            calendarView.setEvents(data)
        }
    }

    override fun inject() {
        component?.inject(this)
    }

    override fun getTitle(): String {
        return getString(R.string.menu_calendar)
    }

}

