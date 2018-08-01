package ru.spcm.apps.womendays.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.WorkerThread
import ru.spcm.apps.womendays.model.db.dao.EventsDao
import ru.spcm.apps.womendays.model.db.dao.SettingsDao
import ru.spcm.apps.womendays.model.dto.Event
import ru.spcm.apps.womendays.model.dto.Setting
import ru.spcm.apps.womendays.model.dto.TodayData
import ru.spcm.apps.womendays.tools.AppExecutors
import ru.spcm.apps.womendays.tools.DateHelper
import ru.spcm.apps.womendays.view.adapters.EventsPagerAdapter
import java.util.*
import javax.inject.Inject

class EventsRepo @Inject
constructor(private val appExecutors: AppExecutors,
            private val eventsDao: EventsDao,
            private val settingsDao: SettingsDao) {

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

    fun updateMonthly(event: Event): LiveData<Long> {
        val result = MutableLiveData<Long>()
        appExecutors.diskIO().execute {
            var value = 0L

            val calendar = DateHelper.getZeroHourCalendar(event.date)
            calendar.add(Calendar.DAY_OF_MONTH, -1)

            //Если сегодня нет отмеченных месячных
            val todayMonthly = eventsDao.getEventMonthlyAtDay(event.date)
            if (todayMonthly == null) {
                //Пробуем получить MONTHLY_CONFIRMED за предыдущий день
                val yesterdayMonthly = eventsDao.getEventMonthlyAtDay(calendar.time)
                if (yesterdayMonthly == null) {
                    //Если таких нет, значит это первый день цикла, нужно сохранить MONTHLY_CONFIRMED на Setting.Type.LENGTH дней и пересчитать циклы на год
                    calendar.timeInMillis = event.date.time

                    eventsDao.clearEvents()

                    val length = settingsDao.getSettingValueInt(Setting.Type.LENGTH.toString())
                    val period = settingsDao.getSettingValueInt(Setting.Type.PERIOD.toString())

                    val eventsList = ArrayList<Event>()
                    for (day in 1..length) {
                        val item = Event(Event.Type.MONTHLY_CONFIRMED)
                        item.date = calendar.time
                        eventsList.add(item)
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }

                    for (month in 1..12) {
                        calendar.add(Calendar.DAY_OF_MONTH, period - length)
                        for (day in 1..length) {
                            val item = Event(Event.Type.MONTHLY)
                            item.date = calendar.time
                            eventsList.add(item)
                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                        }
                    }
                    eventsDao.insert(eventsList)

                } else {
                    //Если есть, значит это продолжение цикла, нужно просто добавить MONTHLY_CONFIRMED, если за этот день таких нет
                    value = eventsDao.insert(event)
                }
            }
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

    fun getLastEvent(): LiveData<Event> {
        return eventsDao.getLastEvent()
    }

    fun getTodayData(): LiveData<TodayData> {
        val data = MutableLiveData<TodayData>()

        appExecutors.diskIO().execute {
            val todayData = TodayData()
            val lastMonthly = getLastMonthly(DateHelper.getZeroHourCalendar())

            if (lastMonthly != null) {
                val period = settingsDao.getSettingValueInt(Setting.Type.PERIOD.toString())

                val lastMonthlyDate = DateHelper.getZeroHourCalendar(lastMonthly.date)
                val now = DateHelper.getZeroHourCalendar()
                var diff = ((now.timeInMillis - lastMonthlyDate.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                if (diff / period >= 1) {
                    diff -= period * (diff / period)
                }
                todayData.cycleDay = diff + 1
                todayData.daysLeft = period - diff
            }
            appExecutors.mainThread().execute {
                data.postValue(todayData)
            }
        }

        return data
    }

    fun getEvents(): LiveData<HashMap<String, Int>> {
        val data = MutableLiveData<HashMap<String, Int>>()
        appExecutors.diskIO().execute {
            val events = HashMap<String, Int>()
            val e = eventsDao.getEvents()
            e.forEach {
                val date = DateHelper.formatYearMonthDay(it.date)
                var flag: Int = events[date] ?: 0

                flag = when (it.type) {
                    Event.Type.SEX_SAFE -> flag or EventsPagerAdapter.FLAG_SEX_SAFE
                    Event.Type.SEX_UNSAFE -> flag or EventsPagerAdapter.FLAG_SEX_UNSAFE
                    Event.Type.MONTHLY -> flag or EventsPagerAdapter.FLAG_MONTHLY
                    Event.Type.MONTHLY_CONFIRMED -> flag or EventsPagerAdapter.FLAG_MONTHLY_CONFIRMED
                }
                events[date] = flag
            }

            setMonthly(events)

            appExecutors.mainThread().execute {
                data.postValue(events)
            }
        }

        return data
    }

    @WorkerThread
    private fun setMonthly(events: HashMap<String, Int>) {
        val calendar = DateHelper.getZeroHourCalendar()
        val lastMonthly = getLastMonthly(calendar)
        if (lastMonthly != null) {
            val period = settingsDao.getSettingValueInt(Setting.Type.PERIOD.toString())
            calendar.timeInMillis = lastMonthly.date.time
            val ovuCalendar = calendar.clone() as Calendar

            calendar.add(Calendar.DAY_OF_MONTH, period)
            ovuCalendar.add(Calendar.DAY_OF_MONTH, period / 2)
            for (i in 1..12) {
                calendar.add(Calendar.DAY_OF_MONTH, period)

                val date = DateHelper.formatYearMonthDay(ovuCalendar.time)
                val flag: Int = events[date] ?: 0
                events[date] = flag or EventsPagerAdapter.FLAG_OVULATION
                ovuCalendar.add(Calendar.DAY_OF_MONTH, period)
            }

        }
    }

    @WorkerThread
    private fun getLastMonthly(calendar: Calendar): Event? {
        var lastMonthly = eventsDao.getLastMonthly(calendar.time)
        do {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val yesterdayEvent = eventsDao.getEventMonthlyAtDay(calendar.time) ?: break
            lastMonthly = yesterdayEvent
        } while (true)
        return lastMonthly
    }

}