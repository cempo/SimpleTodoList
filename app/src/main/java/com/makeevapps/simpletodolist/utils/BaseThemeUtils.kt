package com.makeevapps.simpletodolist.utils

import android.content.Context
import com.makeevapps.simpletodolist.datasource.preferences.PreferenceManager
import com.makeevapps.simpletodolist.enums.ThemeStyle

class BaseThemeUtils(val context: Context, val preferences: PreferenceManager) {

    fun getThemeStyle(): ThemeStyle = ThemeStyle.getThemeById(preferences.getThemeId())

}