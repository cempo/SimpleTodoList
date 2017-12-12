package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.ViewModel
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.BuildConfig
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.utils.DeviceInfo
import javax.inject.Inject

class AboutViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager
    @Inject
    lateinit var deviceInfo: DeviceInfo

    init {
        App.component.inject(this)
    }

    fun getAppVersion(): String = BuildConfig.VERSION_NAME
}