package com.makeevapps.simpletodolist.datasource.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.makeevapps.simpletodolist.enums.ThemeStyle


class PreferenceManager(context: Context) : IDataManager {
    companion object {
        val THEME_ID = "theme_id"
    }

    private var manager: SharedPreferences

    init {
        println("This ($this) is a singleton")

        manager = PreferenceManager.getDefaultSharedPreferences(context)
    }

    constructor(context: Context, manager: SharedPreferences) : this(context) {
        this.manager = manager
    }

    override fun getThemeId(): Int {
        return manager.getInt(THEME_ID, ThemeStyle.defaultValue().id)
    }

    override fun setThemeId(id: Int) {
        manager.edit().putInt(THEME_ID, id).apply()
    }
}
