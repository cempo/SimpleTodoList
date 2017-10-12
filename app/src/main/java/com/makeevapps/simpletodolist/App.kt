package com.makeevapps.simpletodolist

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.makeevapps.simpletodolist.di.AppComponent
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.fabric.sdk.android.Fabric

class App : Application() {
    companion object {
        lateinit var component: AppComponent
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        component = AppComponent.buildAppComponent(this)

        val formatStrategy = PrettyFormatStrategy.newBuilder()
                //.methodCount(3)         // (Optional) How many method line to show. Default 2
                //.methodOffset(5)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag("TAKE_WITH_MOBILE")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean = BuildConfig.DEBUG
        })

        Fabric.with(this, Crashlytics(), Answers())
    }
}