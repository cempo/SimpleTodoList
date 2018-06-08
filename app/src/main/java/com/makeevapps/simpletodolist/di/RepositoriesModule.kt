package com.makeevapps.simpletodolist.di

import com.makeevapps.simpletodolist.datasource.db.TaskDao
import com.makeevapps.simpletodolist.reminders.AlarmScheduler
import com.makeevapps.simpletodolist.repository.TaskRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoriesModule {

    @Provides
    @Singleton
    internal fun provideTaskRepository(taskDao: TaskDao, alarmScheduler: AlarmScheduler): TaskRepository =
            TaskRepository(taskDao, alarmScheduler)

}