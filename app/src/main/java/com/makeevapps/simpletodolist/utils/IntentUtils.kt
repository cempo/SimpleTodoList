package com.makeevapps.simpletodolist.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils

object IntentUtils {

    fun getGooglePlayIntent(context: Context, packageName: String): Intent {
        var intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = Uri.parse("market://details?id=" + packageName)

        if (!isIntentAvailable(context, intent)) {
            intent = IntentUtils.getWebPageIntent("https://play.google.com/store/apps/details?id=" + packageName)
        }

        return intent
    }

    fun getWebPageIntent(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = Uri.parse(url)

        return intent
    }

    fun isIntentAvailable(context: Context, intent: Intent?): Boolean {
        var isAvailable = false
        if (intent != null) {
            try {
                val packageManager = context.packageManager
                val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                //isAvailable = list.size() > 0;
                for (info in list) {
                    if (info.activityInfo.exported) {
                        isAvailable = true
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return intent != null && isAvailable
    }

    fun getEmailIntent(email: String, subject: String, text: String): Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:" + email) // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))

        if (!TextUtils.isEmpty(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (!TextUtils.isEmpty(text)) {
            intent.putExtra(Intent.EXTRA_TEXT, text)
        }

        return intent
    }

}
