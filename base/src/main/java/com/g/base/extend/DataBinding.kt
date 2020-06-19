package com.g.base.extend

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by G on 2017/11/24 0024.
 */
fun <T : ViewDataBinding> ViewGroup.inflateDataBinding(layoutId: Int): T =
        DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, this, false)!!