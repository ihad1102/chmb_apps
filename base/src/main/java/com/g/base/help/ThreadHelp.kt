package com.g.base.help

import com.g.base.extend.mainCalcSchedulers
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Created by G on 2017/11/21 0021.
 */
//工作在子线程完成计算 产生一个值 传给MainThread消费
fun <T> ioResultToMainThread(ioThread: () -> T, mainThread: ((p: T) -> Unit)? = null): Disposable =
        Observable.create<T>(
                {
                    it.onNext(ioThread())
                    it.onComplete()
                })
                .mainCalcSchedulers()
                .subscribe({ mainThread?.invoke(it) }, { e(it.message) })
