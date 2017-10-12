package com.makeevapps.simpletodolist.di

import android.app.Application
import android.content.Context
import com.makeevapps.simpletodolist.datasource.db.AppDatabase
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(val application: Application) {

    @Provides
    @Singleton
    internal fun provideContext(): Context {
        return application
    }

    @Provides
    @Singleton
    internal fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.buildDatabase(context)
    }

    @Provides
    @Singleton
    internal fun providePreferencesManager(context: Context): PreferenceManager {
        return PreferenceManager(context)
    }
}