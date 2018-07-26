package ru.spcm.apps.womendays.repositories

import android.arch.lifecycle.LiveData
import ru.spcm.apps.womendays.model.db.dao.SettingsDao
import ru.spcm.apps.womendays.model.dto.Setting
import ru.spcm.apps.womendays.tools.AppExecutors
import javax.inject.Inject

class SettingsRepo @Inject
constructor(private val appExecutors: AppExecutors,
            private val settingsDao: SettingsDao) {

    fun getSettings(): LiveData<List<Setting>> {
        return settingsDao.getSettings()
    }

    fun save(event: Setting) {
        appExecutors.diskIO().execute {
            settingsDao.insert(event)
        }
    }
}