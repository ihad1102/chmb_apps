package com.g.base.extend

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
import com.g.base.api.ErrorException
import com.g.base.help.Live
import com.g.base.help.ProgressDialog
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.ComputationScheduler
import io.reactivex.internal.schedulers.IoScheduler
import java.util.concurrent.TimeUnit


/**
 * Created by G on 2017/8/14 0014.
 */
object RxScheduler {
    val io = IoScheduler()
    val computation = ComputationScheduler()
    val mainThread: Scheduler = AndroidSchedulers.mainThread()
}

//线程切换
fun <T> Observable<T>.mainIoSchedulers(): Observable<T> =
        subscribeOn(RxScheduler.io).observeOn(RxScheduler.mainThread)

//线程切换
fun <T> Observable<T>.mainCalcSchedulers(): Observable<T> =
        subscribeOn((RxScheduler.computation)).observeOn(RxScheduler.mainThread)

//全在子线程完成
fun <T> Observable<T>.allIoSchedulers(): Observable<T> =
        subscribeOn(RxScheduler.io).observeOn(RxScheduler.io)

//绑定生命周期
fun <C, T : Observable<C>> T.bindLifecycle(owner: LifecycleOwner): Observable<C> {
    return compose(Live<C>(owner))
}

/**
 * 重试多少次
 * 每次间隔多长的时间
 */
@SuppressLint("CheckResult")
fun <T> Observable<T>.retryWithDelay(maxRetries: Int, retryDelayMillis: Long): Observable<T> {
    var retryCount = 0L
    return retryWhen {
        it.flatMap { throwable ->
            return@flatMap if (++retryCount < maxRetries) {
                Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS)
            } else {
                Observable.error(throwable)
            }
        }
    }
}

//显示订阅时显示progressBar
@SuppressLint("CheckResult")
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T> Observable<T>.progressSubscribe(onNext: (T) -> Unit = {}, onError: (Throwable) -> Unit = {}, onComplete: () -> Unit = {}, showError: ((ProgressDialog) -> Unit)? = null, showSucceed: ((ProgressDialog) -> Unit)? = null): Disposable {
    val progressDialog = ProgressDialog()
    return subscribe(
            {
                if (showSucceed != null) {
                    progressDialog.setSucceed("成功") { dialog ->
                        showSucceed(dialog)
                    }
                } else {
                    progressDialog.dismiss(true)
                }
                onNext.invoke(it)
            }
            ,
            {
                if (showError != null) {
                    progressDialog.setFiled((it as? ErrorException)?.message ?: "未知错误!") { dialog ->
                        onError(it)
                        showError(dialog)
                    }
                } else {
                    onError(it)
                    progressDialog.dismiss(true)
                }
            },
            {},
            {
                progressDialog.setStart("正在努力加载中...")
            })
}