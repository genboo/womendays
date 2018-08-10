package ru.spcm.apps.womendays.view.fragments

import android.arch.lifecycle.*
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.View
import kotlinx.android.synthetic.main.layout_app_bar_main.*
import ru.spcm.apps.womendays.App
import ru.spcm.apps.womendays.R
import ru.spcm.apps.womendays.di.components.AppComponent
import ru.spcm.apps.womendays.view.activities.MainActivity
import ru.spcm.apps.womendays.view.components.Navigator
import javax.inject.Inject


abstract class BaseFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var component: AppComponent? = null

    val args: Bundle by lazy { arguments ?: Bundle() }

    val navigator: Navigator by lazy { (activity as MainActivity).navigator }

    protected fun <T : ViewModel> getViewModel(owner: Fragment, viewModelClass: Class<T>): T {
        return ViewModelProviders.of(owner, viewModelFactory).get(viewModelClass)
    }

    protected fun updateToolbar() {
        if (toolbar != null) {
            toolbar.title = getTitle()
            val activity = (activity as MainActivity)
            activity.setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                if (activity.supportFragmentManager.backStackEntryCount > 0) {
                    activity.onBackPressed()
                }
            }
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            if (activity.supportFragmentManager.backStackEntryCount == 0) {
                activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home)
                activity.supportActionBar?.setHomeButtonEnabled(false)
            } else {
                activity.supportActionBar?.setHomeAsUpIndicator(null)
                activity.supportActionBar?.setHomeButtonEnabled(true)
            }
        }
    }

    protected fun initFragment() {
        if (component == null) {
            component = (activity?.application as App).appComponent
            inject()
        }
    }

    protected fun showSnack(text: Int, action: View.OnClickListener?) {
        if (activity != null && view != null) {
            val snackBar = Snackbar.make(view as View, text, Snackbar.LENGTH_LONG)
            if (action != null) {
                snackBar.setAction(ru.spcm.apps.womendays.R.string.action_cancel, action)
            }
            snackBar.show()
        }
    }

    open fun updateTitle(title: String) {
        toolbar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (lifecycle as? LifecycleRegistry)?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (lifecycle as? LifecycleRegistry)?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    internal abstract fun inject()

    abstract fun getTitle(): String
}