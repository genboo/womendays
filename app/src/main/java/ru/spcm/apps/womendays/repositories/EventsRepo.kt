package ru.spcm.apps.womendays.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import ru.spcm.apps.womendays.model.db.dao.EventsDao
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.tools.AppExecutors
import javax.inject.Inject

class EventsRepo @Inject
constructor(private val appExecutors: AppExecutors,
            private val eventsDao: EventsDao) {

    fun save(event: Event): LiveData<Long> {
        val result = MutableLiveData<Long>()
        appExecutors.diskIO().execute {
            val value = eventsDao.insert(event)
            appExecutors.mainThread().execute {
                result.postValue(value)
            }
        }
        return result
    }

    fun delete(event: Event) {
        appExecutors.diskIO().execute {
            eventsDao.delete(event)
        }
    }

    fun getEvents(): LiveData<List<Event>> {
        return eventsDao.getEvents()
    }

}