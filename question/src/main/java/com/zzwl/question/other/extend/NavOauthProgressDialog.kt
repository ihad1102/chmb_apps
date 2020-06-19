package com.zzwl.question.other.extend

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.g.base.appContent
import com.g.base.extend.progressDialog
import com.g.base.extend.progressSubscribe
import com.g.base.help.ProgressDialog
import com.g.base.room.repository.ResultWarp
import com.g.base.router.BasePage
import com.g.base.token.TokenManage
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.disposables.Disposable

@SuppressLint("CheckResult")
@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T> Observable<T>.progressOauthSubscribe(onNext: (T) -> Unit = {}, onError: (Throwable) -> Unit = {}, onComplete: () -> Unit = {}, showSucceed: ((ProgressDialog) -> Unit)? = null): Disposable {
    return progressSubscribe(onNext, onError, onComplete, {
        if (!TokenManage.isOauth()) {
            ARouter.getInstance().build(BasePage.Oauth).navigation(appContent)
        }
        it.dismiss()
    }, showSucceed)
}

fun <T> LiveData<ResultWarp<T>>.progressOauthDialog(onSucceed: ((ProgressDialog) -> Unit)? = null): LiveData<ResultWarp<T>> {
    return progressDialog(onSucceed, {
        if (!TokenManage.isOauth()) {
            ARouter.getInstance().build(BasePage.Oauth).navigation(appContent)
        }
        it.dismiss()
    })
}