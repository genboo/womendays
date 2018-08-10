package ru.spcm.apps.womendays.view.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import ru.spcm.apps.womendays.App
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.di.components.AppComponent
import ru.spcm.apps.womendays.view.components.Navigator

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var component: AppComponent? = null

    val navigator by lazy { Navigator(this, supportFragmentManager, R.id.content) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (component == null) {
            component = (application as App).appComponent
            component?.inject(this)
        }

        bottomMenu.setOnNavigationItemSelectedListener(this)
        navigator.goToToday()
        window.setBackgroundDrawable(null)
    }

    fun getView(): View {
        return mainLayout
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        navigator.backTo()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_calendar -> navigator.goToCalendar()
            R.id.nav_today -> navigator.goToToday()
        }
        return true
    }

}