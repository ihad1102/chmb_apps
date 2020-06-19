package com.g.base.room.repository

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.g.base.api.BaseResult
import com.g.base.api.ErrorCode
import com.g.base.api.ErrorException
import com.g.base.extend.allIoSchedulers
import com.g.base.help.e
import io.reactivex.Observable


class NetworkBoundResult<ResultType>
constructor(
        val loadFromRemote: (() -> Observable<BaseResult<ResultType>>)? = null,
        val loadFormDb: (() -> LiveData<ResultType>)? = null,
        val shouldRemote: ((ResultType?) -> Boolean)? = null,
        val saveRemoteResult: ((ResultType?) -> Unit)? = null,
        val onRemoteSucceed: ((ResultType?) -> Unit)? = null,
        val onRemoteFailed: ((ErrorException) -> Unit)? = null)
    : MediatorLiveData<ResultWarp<ResultType?>>() {
    //数据库源 如果没有实现本地源 模拟本地源
    private val dbSource: LiveData<ResultType>
        get() = (loadFormDb?.invoke() ?: MockDbLiveData<ResultType>())

    init {
        val firstDb = dbSource
        postValue(ResultWarp.loading())
        addSource(firstDb, {
            //在下次操作数据库前 不再观测数据库源的更变
            removeSource(firstDb)
            //先决条件 实现了从远程获取
            //没有实现从数据库获取 直接从网络获取
            //获取策略
            if (loadFromRemote != null &&
                    (loadFormDb == null || (shouldRemote == null && (it == null || (it is List<*> && it.isEmpty()))) || shouldRemote?.invoke(it) == true)) {
                fetchFromRemote()
            } else {
                fetchFromDb()
            }
        })
    }

    override fun postValue(value: ResultWarp<ResultType?>) {
        if (value != getValue())
            super.postValue(value)
    }

    @SuppressLint("CheckResult")
    private fun fetchFromRemote() {
        loadFromRemote!!.invoke()
                .allIoSchedulers()
                .subscribe(
                        {
                            //从远程源获取数据成功
                            onRemoteSucceed?.invoke(it.content)

                            //如果没实现本地源的获取
                            if (loadFormDb == null) {
                                postValue(ResultWarp.success(it.content))
                            } else {
                                saveRemoteResult?.invoke(it.content)
                                fetchFromDb()
                            }
                        },
                        {
                            e(it.message)
                            //从远程源获取数据失败
                            val error: ErrorException = (it as? ErrorException)
                                    ?: ErrorException(ErrorCode.UNKNOWN, "未知错误", null)
                            onRemoteFailed?.invoke(error)

                            //如果没实现本地源的获取
                            if (loadFormDb == null) {
                                postValue(if (error.code == ErrorCode.TOKEN) ResultWarp.oauth() else ResultWarp.error(error))
                            } else {
                                fetchFromDb(if (error.code == ErrorCode.TOKEN) ResultWarp.oauth() else ResultWarp.error(error))
                            }

                        }
                )
    }

    //可能出现在非主线程
    private fun fetchFromDb(resultWarp: ResultWarp<ResultType?>? = null) {
        var isFirst = true
        addSource(dbSource, {
            if (isFirst && resultWarp != null)
                postValue(resultWarp)
            else
                postValue(ResultWarp.success(it))
            isFirst = false
        })
    }
}
