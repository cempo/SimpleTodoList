package com.makeevapps.simpletodolist.di

import android.app.Application
import android.content.Context
import com.makeevapps.simpletodolist.datasource.db.AppDatabase
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.reminders.AlarmScheduler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(val application: Application) {

    @Provides
    @Singleton
    internal fun provideContext(): Context = application

    @Provides
    @Singleton
    internal fun provideAppDatabase(context: Context): AppDatabase = AppDatabase.buildDatabase(context)

    @Provides
    @Singleton
    internal fun providePreferencesManager(context: Context): PreferenceManager = PreferenceManager(context)

    @Provides
    @Singleton
    internal fun provideAlarmScheduler(context: Context): AlarmScheduler = AlarmScheduler(context)
}