package com.makeevapps.simpletodolist.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.afollestad.materialdialogs.MaterialDialog
import com.makeevapps.simpletodolist.R
import com.orhanobut.logger.Logger

object CancelChangesDialog {

    @SuppressLint("StaticFieldLeak")
    private var dialog: MaterialDialog? = null
    private var mHandler: Handler? = null

    @Synchronized
    fun showDialog(context: Context, onSuccess: () -> Unit) {
        dismissDialog()

        val builder = MaterialDialog.Builder(context)
        builder.content(R.string.cancel_your_changes)

        builder.positiveText(R.string.yes)
        builder.onPositive({ dialog, which ->
            onSuccess()
        })

        builder.negativeText(R.string.no)

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