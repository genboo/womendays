package ru.spcm.apps.womendays.di.modules


import android.arch.persistence.room.Room
import android.content.Context

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import ru.spcm.apps.womendays.model.db.Database

/**
 * Инициализация базы данных
 * Created by gen on 28.06.2018.
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


    companion object {
        const val DB_NAME = "womendays.db"
    }
}
