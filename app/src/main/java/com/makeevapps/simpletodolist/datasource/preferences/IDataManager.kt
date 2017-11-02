package com.makeevapps.simpletodolist.datasource.preferences

interface IDataManager {

    fun getThemeId(): String

    fun setThemeId(id: String)

    fun is24HourFormat(): Boolean

    fun setIs24HourFormat(is24: Boolean)

}
