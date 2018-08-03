package ru.spcm.apps.womendays.view.activities

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_first_launch.*
import kotlinx.android.synthetic.main.layout_app_bar_main.*
import kotlinx.android.synthetic.main.layout_main_settings.*
import ru.spcm.apps.womendays.App
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.di.components.AppComponent
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.model.dto.Setting
import ru.spcm.apps.womendays.tools.DateHelper
import ru.spcm.apps.womendays.viewmodel.FirstLaunchViewModel
import java.util.*
import javax.inject.Inject

class FirstLaunchActivity : AppCompatActivity() {

    private var component: AppComponent? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_launch)
        if (component == null) {
            component = (application as App).appComponent
            component?.inject(this)
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = getString(R.string.menu_first_launch)

        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(FirstLaunchViewModel::class.java)
        viewModel.settings.observe(this, Observer { observeSettings(it) })
        viewModel.loadSettings()

        saveSettings.setOnClickListener {
            if (selectedDate != null) {
                viewModel.save(Setting.Type.LENGTH, monthlyLength.text.toString())
                viewModel.save(Setting.Type.PERIOD, monthlyPeriod.text.toString())
                val event = Event(Event.Type.MONTHLY_CONFIRMED)
                event.date = DateHelper.getZeroHourCalendar(selectedDate ?: Date()).time
                viewModel.updateMonthly(event)
                finish()
            }
        }

        monthlyFirstDate.setOnClickListener { selectDate() }

    }

    private fun selectDate() {
        val calendar = DateHelper.getZeroHourCalendar()
        val dateDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, month, dat ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dat)
                    monthlyFirstDate.setText(DateHelper.formatYearMonthDay(calendar.time))
                    selectedDate = calendar.time
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
        dateDialog.create()
        dateDialog.show()
    }

    private fun observeSettings(data: List<Setting>?) {
        data?.forEach {
            when (it.type) {
                Setting.Type.LENGTH -> monthlyLength.setText(it.value)
                Setting.Type.PERIOD -> monthlyPeriod.setText(it.value)
            }
        }
    }

}