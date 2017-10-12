package com.makeevapps.simpletodolist.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.afollestad.materialdialogs.MaterialDialog
import com.makeevapps.simpletodolist.R
import com.orhanobut.logger.Logger

object AlertDialog {

    @SuppressLint("StaticFieldLeak")
    private var dialog: MaterialDialog? = null
    private var mHandler: Handler? = null

    @Synchronized
    fun showPermissionRationaleDialog(context: Context, permissionName: String,
                                      positiveCallback: MaterialDialog.SingleButtonCallback?) {
        val title = R.string.permission_rationale_title
        val text = R.string.permission_rationale_text
        val buttonText = R.string.settings
        showDialog(context, title, text, buttonText, positiveCallback, null)
    }

    @Synchronized
    fun showCancelChangesDialog(context: Context, permissionName: String,
                                      positiveCallback: MaterialDialog.SingleButtonCallback?) {
        val title = R.string.permission_rationale_title
        val text = R.string.permission_rationale_text
        val buttonText = R.string.settings
        showDialog(context, title, text, buttonText, positiveCallback, null)
    }

    /*@Synchronized
    fun showDialog(context: Context, textResId: Int) {
        showDialog(context, textResId, null)
    }

    @Synchronized
    fun showDialog(context: Context, text: String,
                   positiveCallback: MaterialDialog.SingleButtonCallback?) {
        val title = App.instance.getString(R.string.error_dialog_title)

        showDialog(context, title, text, positiveCallback, null)
    }

    @Synchronized
    fun showDialog(context: Context, textResId: Int,
                   positiveCallback: MaterialDialog.SingleButtonCallback?) {
        val title = App.instance.getString(R.string.error_dialog_title)

        var text = ""
        if (textResId != 0) {
            text = context.getString(textResId)
        }

        showDialog(context, title, text, positiveCallback, null)
    }*/

    @Synchronized
    fun showDialog(context: Context, titleResId: Int, textResId: Int, buttonTextResId: Int,
                   positiveCallback: MaterialDialog.SingleButtonCallback?,
                   onDismissListener: DialogInterface.OnDismissListener?) {
        var title = ""
        if (titleResId != 0) {
            title = context.getString(titleResId)
        }

        var text = ""
        if (textResId != 0) {
            text = context.getString(textResId)
        }

        var buttonText = ""
        if (textResId != 0) {
            buttonText = context.getString(buttonTextResId)
        }
        showDialog(context, title, text, buttonText, positiveCallback, onDismissListener)
    }

    @Synchronized
    fun showDialog(context: Context?, title: String, text: String,
                   positiveButtonText: String,
                   positiveCallback: MaterialDialog.SingleButtonCallback?,
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
                builder.positiveText(positiveButtonText)
                builder.onPositive(positiveCallback)
            }

            builder.negativeText(R.string.cancel)

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