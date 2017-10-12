package com.makeevapps.simpletodolist.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils

class DeviceInfo(val context: Context) {
    val sdkVersion: Int
        get() = Build.VERSION.SDK_INT

    val androidVersion: String
        get() = Build.VERSION.RELEASE

    val deviceId: String
        get() = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    val deviceManufacturer: String
        get() {
            val manufacturer = Build.MANUFACTURER
            return capitalize(manufacturer)
        }

    val deviceModel: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL

            if (model != manufacturer && model.startsWith(manufacturer)) {
                return model.substring(manufacturer.length + 1, model.length)
            } else {
                return model
            }
        }

    private fun capitalize(s: String): String {
        if (TextUtils.isEmpty(s)) {
            return ""
        }
        val first = s[0]
        if (Character.isUpperCase(first)) {
            return s
        } else {
            return Character.toUpperCase(first) + s.substring(1)
        }
    }
}
