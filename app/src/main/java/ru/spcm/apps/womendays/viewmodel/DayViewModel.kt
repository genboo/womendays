package ru.spcm.apps.womendays.viewmodel


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.repositiries.EventsRepo
import javax.inject.Inject


/**
 * Created by gen on 23.07.2018.
 */

class DayViewModel @Inject
internal constructor(private val eventsRepo: EventsRepo) : ViewModel() {

    val events = eventsRepo.getEvents()

    fun save(type: Event.Type): LiveData<Long> {
        return eventsRepo.save(Event(type))
    }

    fun delete(id: Long?) {
        if (id != null) {
            eventsRepo.delete(Event(id))
        }
    }

}
