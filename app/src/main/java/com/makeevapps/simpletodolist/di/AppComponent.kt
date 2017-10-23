package com.makeevapps.simpletodolist.di

import android.app.Application
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.receiver.NotificationReceiver
import com.makeevapps.simpletodolist.reminders.ReminderAlarmService
import com.makeevapps.simpletodolist.viewmodel.*
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidSupportInjectionModule::class, AppModule::class, DaoModule::class, RepositoriesModule::class, UtilsModule::class))
interface AppComponent {
    fun inject(app: App)

    fun inject(viewModel: MainViewModel)
    fun inject(viewModel: TodayViewModel)
    fun inject(viewModel: CalendarViewModel)
    fun inject(viewModel: EditTaskViewModel)
    fun inject(viewModel: SettingsViewModel)

    fun inject(service: ReminderAlarmService)
    fun inject(receiver: NotificationReceiver)

    companion object {
        fun buildAppComponent(application: Application) =
                DaggerAppComponent.builder()
                        .appModule(AppModule(application))
                        .daoModule(DaoModule())
                        .repositoriesModule(RepositoriesModule())
                        .utilsModule(UtilsModule())
                        .build()
    }
}