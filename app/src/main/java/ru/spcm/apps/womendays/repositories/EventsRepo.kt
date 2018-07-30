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

    fun delete(event: Event) {
        appExecutors.diskIO().execute {
            eventsDao.delete(event)
        }
    }

    fun getTodayData(): LiveData<TodayData> {
        val data = MutableLiveData<TodayData>()

        appExecutors.diskIO().execute {
            val todayData = TodayData()
            val lastMonthly = eventsDao.getLastMonthly()
            if (lastMonthly != null) {
                val period = settingsDao.getSettingValueInt(Setting.Type.PERIOD.toString())

                val lastMonthlyDate = Calendar.getInstance()
                lastMonthlyDate.timeInMillis = lastMonthly.date.time
                val now = Calendar.getInstance()
                var diff = ((now.timeInMillis - lastMonthlyDate.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                if (diff / period >= 1) {
                    diff -= period * (diff / period)
                }
                diff++
                todayData.cycleDay = diff
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
        val lastMonthly = eventsDao.getLastMonthly()
        if (lastMonthly != null) {
            val length = settingsDao.getSettingValueInt(Setting.Type.LENGTH.toString())
            val period = settingsDao.getSettingValueInt(Setting.Type.PERIOD.toString())

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = lastMonthly.date.time
            val ovuCalendar = calendar.clone() as Calendar

            calendar.add(Calendar.DAY_OF_MONTH, period)
            ovuCalendar.add(Calendar.DAY_OF_MONTH, period / 2)
            for (i in 1..12) {
                for (day in 1..length) {
                    val date = DateHelper.formatYearMonthDay(calendar.time)
                    val flag: Int = events[date] ?: 0
                    events[date] = flag or EventsPagerAdapter.FLAG_MONTHLY
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                calendar.add(Calendar.DAY_OF_MONTH, -length + period)

                val date = DateHelper.formatYearMonthDay(ovuCalendar.time)
                val flag: Int = events[date] ?: 0
                events[date] = flag or EventsPagerAdapter.FLAG_OVULATION
                ovuCalendar.add(Calendar.DAY_OF_MONTH, period)
            }

        }
    }

}