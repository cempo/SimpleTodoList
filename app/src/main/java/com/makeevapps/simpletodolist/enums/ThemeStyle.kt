package com.makeevapps.simpletodolist.enums

import com.makeevapps.simpletodolist.R

enum class ThemeStyle constructor(val id: Int, val themeResId: Int) {
    THEME_1(1, R.style.AppTheme),
    THEME_2(2, R.style.AppThemeDark);

    companion object {
        fun getThemeById(id: Int): ThemeStyle {
            var resultStatus = defaultValue()
            ThemeStyle.values()
                    .filter { id == it.id }
                    .forEach { resultStatus = it }
            return resultStatus
        }

        fun defaultValue(): ThemeStyle = ThemeStyle.THEME_1
    }
}