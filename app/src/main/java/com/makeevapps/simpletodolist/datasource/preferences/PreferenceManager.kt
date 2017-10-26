package com.makeevapps.simpletodolist.datasource.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
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
}
