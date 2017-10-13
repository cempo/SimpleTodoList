package com.makeevapps.simpletodolist.ui.dialog

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.afollestad.materialdialogs.MaterialDialog
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.enums.TaskPriority
import com.orhanobut.logger.Logger

object EditTaskPriorityDialog {

    private var dialog: MaterialDialog? = null
    private var mHandler: Handler? = null

    @Synchronized
    fun showDialog(context: Context, priority: TaskPriority, onSuccess: (priority: TaskPriority) -> Unit) {
        dismissDialog()

        val builder = MaterialDialog.Builder(context)
                .title(R.string.priority)
                .items(TaskPriority.values().asList())
                .itemsCallbackSingleChoice(priority.typeId, MaterialDialog.ListCallbackSingleChoice { dialog, itemView, which, text ->
                    onSuccess(TaskPriority.getPriorityById(which))
                    return@ListCallbackSingleChoice false
                })
                .negativeText(R.string.cancel)

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