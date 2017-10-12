package com.makeevapps.simpletodolist.enums

import com.makeevapps.simpletodolist.R

enum class MainMenuItemType constructor(val id: Long, val textResId: Int, val imageResId: Int) {
    TODAY(1, R.string.today, R.drawable.ic_today_black_24dp),
    CALENDAR(2, R.string.calendar, R.drawable.ic_date_range_black_24dp),
    SETTINGS(3, R.string.settings, R.drawable.ic_settings_black_24dp);

    companion object {
        fun getItemById(id: Long): MainMenuItemType {
            var resultStatus = defaultValue()
            MainMenuItemType.values()
                    .filter { id == it.id }
                    .forEach { resultStatus = it }
            return resultStatus
        }

        fun defaultValue(): MainMenuItemType = MainMenuItemType.TODAY
    }
}