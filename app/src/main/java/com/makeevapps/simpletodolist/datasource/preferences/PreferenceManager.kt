package com.makeevapps.simpletodolist.datasource.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.format.DateFormat
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.enums.ThemeStyle


class PreferenceManager(val context: Context) : IDataManager {
    private var manager: SharedPreferences

    init {
        manager = PreferenceManager.getDefaultSharedPreferences(context)
    }

    constructor(context: Context, manager: SharedPreferences) : this(context) {
        this.manager = manager
    }

    override fun getThemeId(): String {
        return manager.getString(context.getString(R.string.themeId), ThemeStyle.defaultValue().id)
    }

    override fun setThemeId(id: String) {
        manager.edit().putString(context.getString(R.string.themeId), id).apply()
    }

    override fun is24HourFormat(): Boolean {
        return manager.getBoolean(context.getString(R.string.is24HourFormat), DateFormat.is24HourFormat(context))
    }

    override fun setIs24HourFormat(is24: Boolean) {
        manager.edit().putBoolean(context.getString(R.string.is24HourFormat), is24).apply()
    }

    fun getSharedPreferences() = manager
}
