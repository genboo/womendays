package ru.spcm.apps.womendays.di.components


import android.content.Context

import javax.inject.Singleton

import dagger.BindsInstance
import dagger.Component
import ru.spcm.apps.womendays.di.ViewModelModule
import ru.spcm.apps.womendays.di.modules.DbModule
import ru.spcm.apps.womendays.di.modules.NavigationModule
import ru.spcm.apps.womendays.view.activities.FirstLaunchActivity
import ru.spcm.apps.womendays.view.activities.MainActivity
import ru.spcm.apps.womendays.view.activities.SettingsActivity
import ru.spcm.apps.womendays.view.fragments.BaseFragment

/**
 * Компонент di
 * Created by gen on 18.07.2018.
 */
@Component(modules = [ViewModelModule::class, DbModule::class, NavigationModule::class])
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(activity: MainActivity)

    fun inject(activity: SettingsActivity)

    fun inject(activity: FirstLaunchActivity)

    fun inject(fragment: BaseFragment)

}