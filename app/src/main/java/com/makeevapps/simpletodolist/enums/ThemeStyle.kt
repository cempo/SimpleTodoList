package com.makeevapps.simpletodolist.enums

import com.makeevapps.simpletodolist.R

enum class ThemeStyle constructor(val id: String, val themeResId: Int) {
    THEME_1("1", R.style.AppTheme1),
    THEME_2("2", R.style.AppTheme2);

    companion object {
        fun getThemeById(id: String): ThemeStyle {
            var resultStatus = defaultValue()
            ThemeStyle.values()
                    .filter { id == it.id }
                    .forEach { resultStatus = it }
            return resultStatus
        }

        fun defaultValue(): ThemeStyle = ThemeStyle.THEME_1
    }
}