package com.g.base.room.repository

import android.arch.lifecycle.MutableLiveData

//300ms没有接收到数据发射一个空值
class MockDbLiveData<T> : MutableLiveData<T>() {
    private var hasSendEmpty = false

    override fun onActive() {
        super.onActive()
        if (!hasSendEmpty){
            if (value == null){
                hasSendEmpty = true
                postValue(null)
            }
        }
    }
}