package ru.spcm.apps.womendays.viewmodel


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.model.dto.TodayData
import ru.spcm.apps.womendays.repositories.EventsRepo
import ru.spcm.apps.womendays.tools.AbsentLiveData
import javax.inject.Inject


/**
 * Created by gen on 23.07.2018.
 */

class DayViewModel @Inject
internal constructor(private val eventsRepo: EventsRepo) : ViewModel() {

    private val eventsSwitcher = MutableLiveData<Boolean>()
    val events: LiveData<HashMap<String, Int>>
    val data: LiveData<TodayData> = eventsRepo.getTodayData()
    val eventsObserver: LiveData<Event> = eventsRepo.getLastEvent()

    init {
        events = Transformations.switchMap(eventsSwitcher) {
            if (it) {
                return@switchMap eventsRepo.getEvents()
            }
            return@switchMap AbsentLiveData.create<HashMap<String, Int>>()
        }

    }

    fun loadEvents() {
        eventsSwitcher.postValue(true)
    }

    fun save(type: Event.Type): LiveData<Long> {
        return eventsRepo.save(Event(type))
    }

    fun delete(id: Long?) {
        if (id != null) {
            eventsRepo.delete(Event(id))
        }
    }

}
