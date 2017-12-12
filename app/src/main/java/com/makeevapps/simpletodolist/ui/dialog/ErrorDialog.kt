package com.makeevapps.simpletodolist.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.makeevapps.simpletodolist.App
import com.makeevapps.simpletodolist.R
import com.orhanobut.logger.Logger

object ErrorDialog {

    @SuppressLint("StaticFieldLeak")
    private var dialog: MaterialDialog? = null
    private var mHandler: Handler? = null

    @Synchronized
    fun showDialog(context: Context, text: String) {
        showDialog(context, text, null)
    }

    @Synchronized
    fun showDialog(context: Context, textResId: Int) {
        showDialog(context, textResId, null)
    }

    @Synchronized
    fun showDialog(context: Context, text: String,
                   positiveCallback: SingleButtonCallback?) {
        val title = App.instance.getString(R.string.error_dialog_title)

        showDialog(context, title, text, positiveCallback, null)
    }

    @Synchronized
    fun showDialog(context: Context, textResId: Int,
                   positiveCallback: SingleButtonCallback?) {
        val title = App.instance.getString(R.string.error_dialog_title)

        var text = ""
        if (textResId != 0) {
            text = context.getString(textResId)
        }

        showDialog(context, title, text, positiveCallback, null)
    }

    @Synchronized
    fun showDialog(context: Context, titleResId: Int, textResId: Int,
                   positiveCallback: SingleButtonCallback,
                   onDismissListener: DialogInterface.OnDismissListener) {
        var title = ""
        if (titleResId != 0) {
            title = context.getString(titleResId)
        }

        var text = ""
        if (textResId != 0) {
            text = context.getString(textResId)
        }
        showDialog(context, title, text, positiveCallback, onDismissListener)
    }

    @Synchronized
    fun showDialog(context: Context?, title: String, text: String,
                   positiveCallback: SingleButtonCallback?,
                   onDismissListener: DialogInterface.OnDismissListener?) {
        dismissDialog()

        if (context != null) {
            val builder = MaterialDialog.Builder(context)
            if (!TextUtils.isEmpty(title)) {
                builder.title(title)
            }
            if (!TextUtils.isEmpty(text)) {
                builder.content(text)
            }

            if (positiveCallback != null) {
                builder.positiveText(android.R.string.ok)
                builder.onPositive(positiveCallback)
            }

            if (onDismissListener != null) {
                builder.dismissListener(onDismissListener)
            }

            if (mHandler == null)
                mHandler = Handler(Looper.getMainLooper())

            mHandler!!.post {
                try {
                    dialog = builder.show()
                } catch (e: Exception) {
                    Logger.e("Show error dialog: $e")
                }
            }
        }
    }

    @Synchronized
    fun dismissDialog() {
        if (dialog != null && dialog!!.isShowing) {
            try {
                dialog!!.dismiss()
            } catch (e: IllegalArgumentException) {
                Logger.e("Dismiss error dialog: $e")
            }

        }
    }
}