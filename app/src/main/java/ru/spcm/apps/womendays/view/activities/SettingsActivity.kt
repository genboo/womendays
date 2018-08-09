package ru.spcm.apps.womendays.view.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.layout_app_bar_main.*
import kotlinx.android.synthetic.main.layout_main_settings.*
import ru.spcm.apps.womendays.App
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.di.components.AppComponent
import ru.spcm.apps.womendays.model.dto.Setting
import ru.spcm.apps.womendays.viewmodel.SettingsViewModel
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

    private var component: AppComponent? = null
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        window.setBackgroundDrawable(null)
        if (component == null) {
            component = (application as App).appComponent
            component?.inject(this)
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(null)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = getString(R.string.menu_settings)

        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel::class.java)
        viewModel.settings.observe(this, Observer { observeSettings(it) })
        viewModel.loadSettings()

        saveSettings.setOnClickListener {
            viewModel.save(Setting.Type.LENGTH, monthlyLength.text.toString())
            viewModel.save(Setting.Type.PERIOD, monthlyPeriod.text.toString())
            Snackbar.make(mainLayout, R.string.action_saved, Snackbar.LENGTH_LONG).show()
        }

    }

    private fun observeSettings(data: List<Setting>?) {
        data?.forEach {
            when (it.type) {
                Setting.Type.LENGTH -> monthlyLength.setText(it.value)
                Setting.Type.PERIOD -> monthlyPeriod.setText(it.value)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}