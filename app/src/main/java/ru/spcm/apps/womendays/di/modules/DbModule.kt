package ru.spcm.apps.womendays.di.modules


import android.arch.persistence.room.Room
import android.content.Context

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import ru.spcm.apps.womendays.model.db.Database
import ru.spcm.apps.womendays.model.db.dao.EventsDao

/**
 * Инициализация базы данных
 * Created by gen on 18.07.2018.
 */
@Suppress("unused")
@Module
class DbModule {

    /**
     * Базовый провайдер базы данных
     *
     * @param context Контекст
     * @return База данных
     */
    @Provides
    @Singleton
    internal fun provideDatabase(context: Context): Database {
        return Room
                .databaseBuilder(context, Database::class.java, DB_NAME)

                .build()
    }

    @Provides
    @Singleton
    internal fun provideEventsDao(db: Database): EventsDao {
        return db.eventsDao()
    }


    companion object {
        const val DB_NAME = "womendays.db"
    }
}
