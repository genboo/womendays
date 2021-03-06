package ru.spcm.apps.womendays.view.fragments

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fragment_today.*
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.model.dto.EventsData
import ru.spcm.apps.womendays.model.dto.TodayData
import ru.spcm.apps.womendays.view.activities.FirstLaunchActivity
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
        viewModel.eventsObserver.observe(this, Observer { observeEvent(it, viewModel) })

        popup.setOnToggleListener {
            if (it) {
                fab.fadeOut()
            } else {
                fab.fadeIn()
            }
        }

        popup.setOnSaveListener { type, message ->
            val event = Event(type)
            event.message = message
            viewModel.save(event).observe(this, Observer { id ->
                if (id != null) {
                    showSnack(R.string.action_added, View.OnClickListener { viewModel.delete(id) })
                }
            })
        }

        fab.setOnClickListener { popup.toggle() }

        dayWidget.setAddMonthlyListener(View.OnClickListener { _ ->
            viewModel.updateMonthly(Event(Event.Type.MONTHLY_CONFIRMED))
                    .observe(this, Observer { showSnack(R.string.action_added, null) })
        })
    }

    private fun observeEvent(data: Event?, viewModel: DayViewModel) {
        if (data == null) {
            startActivity(Intent(requireContext(), FirstLaunchActivity::class.java))
        } else {
            viewModel.loadEvents()
        }
    }

    private fun observeTodayData(data: TodayData?) {
        if (data != null) {
            dayWidget.setData(data)
            dayWidget.visibility = View.VISIBLE
        }
    }

    private fun observeEvents(data: EventsData?) {
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

}

