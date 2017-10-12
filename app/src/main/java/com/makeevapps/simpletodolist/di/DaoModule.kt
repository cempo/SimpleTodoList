package com.makeevapps.simpletodolist.di

import com.makeevapps.simpletodolist.datasource.db.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DaoModule {

    @Provides
    @Singleton
    internal fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

}