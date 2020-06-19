package com.g.base.extend

import android.arch.lifecycle.*
import com.g.base.api.ErrorCode
import com.g.base.api.ErrorException
import com.g.base.help.ProgressDialog
import com.g.base.room.repository.ResultWarp
import com.g.base.room.repository.Status
import io.reactivex.Observable

/**
 * Created by G on 2017/11/18 0018.
 */
fun <T> LiveData<ResultWarp<T>>.observeEx(owner: LifecycleOwner, obs: (ResultWarp<T>) -> Unit) {
    observe(owner, Observer { obs.invoke(it!!) })
}

fun <T> LiveData<ResultWarp<T>>.observeExOnce(owner: LifecycleOwner, obs: (ResultWarp<T>) -> Unit) {
    var onceObs: Observer<ResultWarp<T>>? = null
    onceObs = Observer {
        if (it != null) {
            if (it.status != Status.Loading)
                removeObserver(onceObs!!)
            obs.invoke(it)
        }
    }
    observe(owner,onceObs)
}

fun <T> LiveData<T>.observeNullableEx(owner: LifecycleOwner, obs: (T?) -> Unit) {
    observe(owner, Observer { obs.invoke(it) })
}

fun <T> LiveData<T>.observeNullableExOnce(obs: (T?) -> Unit) {
    var onceObs: ((T?) -> Unit)? = null
    onceObs = {
        if (it != null) {
            if (it !is ResultWarp<*> || it.status !== Status.Loading)
                removeObserver(onceObs!!)
            obs.invoke(it)
        }
    }
    observeForever(onceObs)
}

fun <T> LiveData<ResultWarp<T>>.progressDialog(onSucceed: ((ProgressDialog) -> Unit)? = null, onError: ((ProgressDialog) -> Unit)? = null): LiveData<ResultWarp<T>> {
    val progressDialog = ProgressDialog()
    var status = 0
    return map {
        status++
        when (it.status) {
            Status.Loading -> {
                if (status == 1) {
                    progressDialog.setStart("正在努力加载中...")
                }
            }
            Status.Content -> {
                if (status == 2) {
                    if (onSucceed != null) {
                        progressDialog.setSucceed("成功") {
                            onSucceed(it)
                        }
                    } else {
                        progressDialog.dismiss(true)
                    }
                }
            }
            else -> {
                if (status == 2) {
                    if (onError != null) {
                        progressDialog.setFiled(it.error?.message ?: "未知错误!") {
                            onError(it)
                        }
                    } else {
                        progressDialog.dismiss(true)
                    }
                }
            }
        }
        it
    }
}

fun <T> LiveData<ResultWarp<T>>.toObservable(lifecycleOwner: LifecycleOwner, throwError: Boolean = false, filterOauth: Boolean = true): Observable<ResultWarp<T>> =
        Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, this))
                .filter {
                    if (throwError && (it.status == Status.Error || it.status == Status.Oauth))
                        throw  it.error!!
                    else
                        !(it.status == Status.Loading || (filterOauth && it.status == Status.Oauth))
                }

//返回的数据不能为空 List长度不能为0
fun <T> LiveData<ResultWarp<T?>>.resultNotNull(): LiveData<ResultWarp<T?>> = Transformations.map(this) {
    if (it.status == Status.Content && (it.data == null || (it.data as? List<*>)?.isEmpty() == true)) {
        ResultWarp.error(ErrorException(ErrorCode.EMPTY, "暂时找不到相关的数据~", null))
    } else {
        it
    }
}

fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>): LiveData<Y> = Transformations.switchMap(this, { func.invoke(it) })
fun <X, Y> LiveData<X>.map(func: (X) -> Y): LiveData<Y> = Transformations.map(this, func)
