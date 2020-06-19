@file:Suppress("UNCHECKED_CAST")

package com.g.base.help

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import com.g.base.extend.observeNullableEx

/**
 * Created by G on 2017/11/21 0021.
 */
private class SimpleLiveNotifyData(var consumed: Boolean,var data: Any?)
private val simpleLiveNotifyData by lazy { MutableLiveData<HashMap<String, SimpleLiveNotifyData>>().also { it.value = hashMapOf() } }

/**
 * exec 返回 true 表示事件已经被消费彻底不需要向下传递了!
 */
fun <T> observeSimpleNotify(owner: LifecycleOwner, url: String, exec: (data: T?) -> Boolean) {
    simpleLiveNotifyData.observeNullableEx(owner){
        val data: SimpleLiveNotifyData? = it?.get(url)
        if (data?.consumed == false){
            data.consumed = exec.invoke(data.data as? T)
        }
    }
}

fun postSimpleNotify(url: String, data: Any? = null) {
    simpleLiveNotifyData.value?.put(url, SimpleLiveNotifyData(false, data))
    simpleLiveNotifyData.postValue(simpleLiveNotifyData.value)
}

