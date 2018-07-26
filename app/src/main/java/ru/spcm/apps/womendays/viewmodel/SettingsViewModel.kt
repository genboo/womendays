package ru.spcm.apps.womendays.viewmodel


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import ru.spcm.apps.womendays.model.dto.Setting
import ru.spcm.apps.womendays.repositories.SettingsRepo
import ru.spcm.apps.womendays.tools.AbsentLiveData
import javax.inject.Inject


/**
 * Created by gen on 23.07.2018.
 */

class SettingsViewModel @Inject
internal constructor(private val settingsRepo: SettingsRepo) : ViewModel() {

    private val settingsSwitcher = MutableLiveData<Boolean>()
    val settings: LiveData<List<Setting>>

    init {
        settings = Transformations.switchMap(settingsSwitcher) {
            if (it) {
                return@switchMap settingsRepo.getSettings()
            }
            return@switchMap AbsentLiveData.create<List<Setting>>()
        }
    }

    fun loadSettings() {
        settingsSwitcher.postValue(true)
    }

    fun save(type: Setting.Type, value: String) {
        settingsRepo.save(Setting(type, value))
    }

}
