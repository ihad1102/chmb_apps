package com.g.base.dataBinding

import android.databinding.BindingConversion
import android.view.View


/**
 * Created by G on 2017/11/23 0023.
 */
@BindingConversion
fun convertNumberToString(number: Number?) = number?.toString()?:""

@BindingConversion
fun convertBoolToVisible(boolean: Boolean): Int = if (boolean) View.VISIBLE else View.GONE
