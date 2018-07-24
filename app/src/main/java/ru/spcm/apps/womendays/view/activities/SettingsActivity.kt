package ru.spcm.apps.womendays.view.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_app_bar_main.*
import ru.spcm.apps.womendays.App
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.di.components.AppComponent

class SettingsActivity : AppCompatActivity() {

    private var component: AppComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        window.setBackgroundDrawableResource(R.drawable.transparent)
        if (component == null) {
            component = (application as App).appComponent
            component?.inject(this)
        }

        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.menu_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(null)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}