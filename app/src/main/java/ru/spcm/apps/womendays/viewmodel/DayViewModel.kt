package ru.spcm.apps.womendays.viewmodel


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.model.dto.EventsData
import ru.spcm.apps.womendays.model.dto.TodayData
import ru.spcm.apps.womendays.repositories.EventsRepo
import ru.spcm.apps.womendays.tools.AbsentLiveData
import java.util.*
import javax.inject.Inject


/**
 * Created by gen on 23.07.2018.
 */

class DayViewModel @Inject
internal constructor(private val eventsRepo: EventsRepo) : ViewModel() {

    private val eventsSwitcher = MutableLiveData<Boolean>()
    private val dateSwitcher = MutableLiveData<Date>()

    val events: LiveData<EventsData>
    val data: LiveData<TodayData>
    val eventsObserver: LiveData<Event> = eventsRepo.getLastEvent()
    val eventsByDay: LiveData<List<Event>>

    init {
        events = Transformations.switchMap(eventsSwitcher) {
            if (it) {
                return@switchMap eventsRepo.getEvents()
            }
            return@switchMap AbsentLiveData.create<EventsData>()
        }

        data = Transformations.switchMap(eventsSwitcher) {
            if (it) {
                return@switchMap eventsRepo.getTodayData()
            }
            return@switchMap AbsentLiveData.create<TodayData>()
        }

        eventsByDay = Transformations.switchMap(dateSwitcher) {
            if (it != null) {
                return@switchMap eventsRepo.getEventsByDate(it)
            }
            return@switchMap AbsentLiveData.create<List<Event>>()
        }
    }

    fun loadEventsByDate(date: Date) {
        dateSwitcher.postValue(date)
    }

    fun loadEvents() {
        eventsSwitcher.postValue(true)
    }

    fun save(type: Event.Type): LiveData<Long> {
        return eventsRepo.save(Event(type))
    }

    fun updateMonthly(event: Event): LiveData<Long> {
        return eventsRepo.updateMonthly(event)
    }

    fun delete(id: Long?) {
        if (id != null) {
            eventsRepo.delete(Event(id))
        }
    }

}
