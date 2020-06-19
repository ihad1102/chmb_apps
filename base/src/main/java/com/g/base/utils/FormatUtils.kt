package com.g.base.utils

import android.widget.EditText
import java.util.regex.Pattern

fun isPhoneNumber(phoneNumber: String): Boolean {
    val pattern = Pattern.compile("^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$")
    val matcher = pattern.matcher(phoneNumber)
    return matcher.matches()
}

//只允许edittext输入digits位小数
fun limitDecimal(s: String?, editText: EditText, digits: Int) {
    //删除“.”后面超过2位后的数据
    if (s?.contains(".") == true) {
        if (s.length - 1 - s.indexOf(".") > digits) {
            val s1 = s.subSequence(0,
                    s.indexOf(".") + digits + 1)
            editText.setText(s1)
            editText.setSelection(s1.length) //光标移到最后
        }
    }
    //如果"."在起始位置,则起始位置自动补0
    if (s?.trim()?.substring(0) == ".") {
        val s1 = "0$s"
        editText.setText(s1)
        editText.setSelection(2)
    }

    //如果起始位置为0,且第二位跟的不是".",则无法后续输入
    if (s?.startsWith("0") == true
            && s.trim().length > 1) {
        if (s.substring(1, 2) != ".") {
            editText.setText(s.subSequence(0, 1))
            editText.setSelection(1)
            return
        }
    }
}


