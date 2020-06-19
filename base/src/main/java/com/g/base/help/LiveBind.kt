package com.g.base.help


import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.support.annotation.MainThread
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class Live<T>(private var lifecycleOwner: LifecycleOwner) : ObservableTransformer<T, T>, LifecycleObserver {
    private val subject = PublishSubject.create<T>()
    private var disposable: Disposable? = null
    private var data: T? = null
    private var error: Throwable? = null
    private var active: Boolean = false
    private var version = -1
    private var lastVersion = -1

    @MainThread
    override fun apply(@NonNull upstream: Observable<T>): ObservableSource<T> {
        if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.DESTROYED) {
            lifecycleOwner.lifecycle.addObserver(this)
            disposable = upstream.subscribe(
                    {
                        ++version
                        data = it
                        considerNotify()
                    },
                    {
                        ++version
                        error = it
                        considerNotify()
                    },
                    {
                        removeObserver()
                    })
            return subject.doOnDispose { disposable?.dispose(); disposable = null }
        } else {
            return Observable.empty()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    internal fun onStateChange() {
        if (this.lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            removeObserver()
        } else {
            activeStateChanged(isActiveState(lifecycleOwner.lifecycle.currentState))
        }
    }

    private fun removeObserver() {
        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
        }
        lifecycleOwner.lifecycle.removeObserver(this)
    }

    private fun activeStateChanged(newActive: Boolean) {
        if (newActive != active) {
            active = newActive
            considerNotify()
        }
    }

    private fun considerNotify() {
        if (active) {
            if (isActiveState(lifecycleOwner.lifecycle.currentState)) {
                if (lastVersion < version) {
                    lastVersion = version
                    if (error != null) {
                        subject.onError(error!!)
                        removeObserver()
                    } else if (data != null && disposable != null && !disposable!!.isDisposed) {
                        subject.onNext(data!!)
                    }
                }
            }
        }
    }

    private fun isActiveState(state: Lifecycle.State): Boolean {
        return state.isAtLeast(Lifecycle.State.STARTED)
    }
}