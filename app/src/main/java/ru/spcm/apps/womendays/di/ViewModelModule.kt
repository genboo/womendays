package ru.spcm.apps.womendays.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.spcm.apps.womendays.viewmodel.DayViewModel
import ru.spcm.apps.womendays.viewmodel.FirstLaunchViewModel
import ru.spcm.apps.womendays.viewmodel.SettingsViewModel

@Suppress("unused")
@Module
internal abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(DayViewModel::class)
    internal abstract fun bindDayViewModel(viewModel: DayViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FirstLaunchViewModel::class)
    internal abstract fun bindFirstLaunchViewModel(viewModel: FirstLaunchViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}