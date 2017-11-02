package com.makeevapps.simpletodolist.viewmodel

import android.arch.lifecycle.ViewModel
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.enums.ThemeStyle
import javax.inject.Inject

class SettingsViewModel : ViewModel() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    init {
        App.component.inject(this)
    }

    fun saveThemeId(themeId: String) {
        preferenceManager.setThemeId(themeId)
    }

    fun saveIs24HourFormat(is24: Boolean) {
        preferenceManager.setIs24HourFormat(is24)
    }
}