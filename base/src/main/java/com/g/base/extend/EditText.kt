package com.g.base.extend

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Created by G on 2017/9/19 0019.
 */
fun EditText.showKeyboard(show: Boolean) {

    //调用系统输入法
    val inputManager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    if (show){
        //设置可获得焦点
        this.isFocusable = true
        this.isFocusableInTouchMode = true
        //请求获得焦点
        this.requestFocus()
        inputManager.showSoftInput(this, 0)
    }
    else{
        inputManager.hideSoftInputFromWindow(windowToken,0)
    }
}