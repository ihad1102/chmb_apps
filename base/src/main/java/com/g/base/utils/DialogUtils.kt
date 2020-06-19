package com.g.base.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.g.base.R
import com.g.base.help.tryCatch


object DialogUtils {
    //todo 底部dialog,自定义dialog,popupwindow封装
// list 是不是可以为一种特殊的自定义view,
//https://www.ctolib.com/CustomPopwindow.html
    fun build(activity: Activity,
              title: String = "",
              msg: String,
              view: View? = null,
              positiveText: String = "确定",
              onPositive: (dialog: DialogInterface) -> Unit,
              cancelText: String = "取消",
              onCancel: (() -> Unit)? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        if (title.isNotEmpty())
            builder.setTitle(title)
        if (view != null)
            builder.setView(view)
        builder.setMessage(msg)
        builder.setPositiveButton(positiveText) { dialog, _ ->
            onPositive(dialog)
        }

        builder.setNegativeButton(cancelText) { _, _ ->
            if (onCancel != null)
                onCancel()
        }
        val dialog = builder.create()
        dialog.show()
        setMsgTextColor(dialog)
        return dialog
    }

    private fun setMsgTextColor(dialog: AlertDialog) {
        tryCatch {
            val mAlert = AlertDialog::class.java.getDeclaredField("mAlert")
            mAlert.isAccessible = true
            val mAlertController = mAlert.get(dialog)
            val mMessage = mAlertController.javaClass.getDeclaredField("mMessageView")
            mMessage.isAccessible = true
            val mMessageView = mMessage.get(mAlertController) as TextView
            mMessageView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
            mMessageView.setTextColor(ContextCompat.getColor(dialog.context, R.color.colorTextWeekDark))
        }
    }

    fun buildBottomDialog(activity: Activity, view: View): Dialog {
        val bottomDialog = Dialog(activity)
        bottomDialog.setContentView(view)
        bottomDialog.setCanceledOnTouchOutside(true)
        bottomDialog.show()
        val window = bottomDialog.window
        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawableResource(android.R.color.transparent)//不设置背景色 padding不生效,原因未知//TODO
        window.decorView.setPadding(0, 0, 0, 0)
        val layoutParams = window.attributes
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = layoutParams
        window.setWindowAnimations(com.g.base.R.style.BottomDialog_Animation)
        return bottomDialog
    }
}